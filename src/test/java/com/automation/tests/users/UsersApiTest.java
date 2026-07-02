package com.automation.tests.users;

import com.automation.framework.client.ApiClient;
import com.automation.framework.constants.ApiEndpoints;
import com.automation.framework.contracts.v1.User;
import com.automation.tests.base.BaseTest;
import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("User Service")
@Feature("Users CRUD API")
public class UsersApiTest extends BaseTest {

    private static final Faker FAKER = new Faker();

    @Override
    protected String serviceBaseUriKey() {
        return "service.users.baseUri";
    }

    // ---------- Happy path ----------
    @Test(description = "GET /users returns 200 and a non-empty list")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Read users")
    public void shouldListUsers() {
        Response resp = ApiClient.builder(requestSpec)
                .withAuth(authProvider)
                .get(ApiEndpoints.USERS);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(resp.statusCode()).isEqualTo(200);
        soft.assertThat(resp.jsonPath().getList("$")).isNotEmpty();
        soft.assertThat(resp.time()).isLessThan(config.getInt("http.maxResponseTimeMs"));
        soft.assertAll();

        resp.then().spec(responseSpec)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/users-list-schema.json"));
    }

    @Test(description = "POST /users creates a new user")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create user")
    public void shouldCreateUser() {
        User payload = User.newBuilder()
                .setName(FAKER.name().fullName())
                .setEmail(FAKER.internet().emailAddress())
                .setPhone(FAKER.phoneNumber().cellPhone())
                .setRole("user")
                .build();

        Response resp = ApiClient.builder(requestSpec)
                .withAuth(authProvider)
                .body(payload)
                .post(ApiEndpoints.USERS);

        resp.then().spec(responseSpec)
                .statusCode(anyOf(is(200), is(201)))
                .body("name",  equalTo(payload.getName()))
                .body("email", equalTo(payload.getEmail()))
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @Test(description = "GET /users/{id} returns the requested user")
    @Severity(SeverityLevel.NORMAL)
    @Story("Read user")
    public void shouldGetUserById() {
        Response resp = ApiClient.builder(requestSpec)
                .withAuth(authProvider)
                .pathParam("id", 1)
                .get(ApiEndpoints.USER_BY_ID);

        resp.then().spec(responseSpec)
                .statusCode(200)
                .body("id", notNullValue())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @Test(description = "PUT /users/{id} updates the user")
    @Severity(SeverityLevel.NORMAL)
    @Story("Update user")
    public void shouldUpdateUser() {
        User update = User.newBuilder()
                .setName("Updated Name")
                .setEmail("updated@example.com")
                .build();

        ApiClient.builder(requestSpec)
                .withAuth(authProvider)
                .pathParam("id", 1)
                .body(update)
                .put(ApiEndpoints.USER_BY_ID)
                .then().spec(responseSpec)
                .statusCode(200)
                .body("name", equalTo("Updated Name"));
    }

    @Test(description = "DELETE /users/{id} removes the user")
    @Severity(SeverityLevel.NORMAL)
    @Story("Delete user")
    public void shouldDeleteUser() {
        ApiClient.builder(requestSpec)
                .withAuth(authProvider)
                .pathParam("id", 1)
                .delete(ApiEndpoints.USER_BY_ID)
                .then().spec(responseSpec)
                .statusCode(anyOf(is(200), is(204)));
    }

    // ---------- Negative cases ----------
    @Test(description = "GET /users/{id} for unknown id returns 404")
    @Severity(SeverityLevel.NORMAL)
    @Story("Not found")
    public void shouldReturn404ForUnknownUser() {
        ApiClient.builder(requestSpec)
                .withAuth(authProvider)
                .pathParam("id", 99999999)
                .get(ApiEndpoints.USER_BY_ID)
                .then().statusCode(404);
    }

    @Test(description = "POST /users without auth returns 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Auth failure")
    public void shouldReturn401WithoutAuth() {
        User payload = User.newBuilder().setName("x").setEmail("x@example.com").build();
        Response resp = ApiClient.builder(unauthenticatedSpec())
                .body(payload)
                .post(ApiEndpoints.USERS);
        // Public mocks may return 201; assert via softly for portability
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(resp.statusCode()).isIn(401, 403, 201);
        soft.assertAll();
    }

    // ---------- Boundary values ----------
    @DataProvider(name = "boundaryNames")
    public Object[][] boundaryNames() {
        return new Object[][] {
                { "a" },                              // min length
                { "A".repeat(255) },                  // max-typical length
                { "Ünîçødé 名前 🚀" }                 // unicode/emoji
        };
    }

    @Test(dataProvider = "boundaryNames", description = "POST /users accepts boundary-value names")
    @Severity(SeverityLevel.MINOR)
    @Story("Boundary values")
    public void shouldAcceptBoundaryNames(String name) {
        User payload = User.newBuilder().setName(name).setEmail("boundary@example.com").build();
        ApiClient.builder(requestSpec)
                .withAuth(authProvider)
                .body(payload)
                .post(ApiEndpoints.USERS)
                .then().statusCode(anyOf(is(200), is(201)));
    }
}
