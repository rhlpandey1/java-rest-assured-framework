package com.automation.tests.users;

import com.automation.framework.pojo.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class E2ERestAssuredDemoTest {

    // ============================================================
    // 1. REQUEST / RESPONSE SPECIFICATION
    // ============================================================

    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    @BeforeClass
    public void setup() {

        // Actual working public API
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";


        // --------------------------------------------------------
        // Request Specification
        // --------------------------------------------------------

        requestSpec =
                new RequestSpecBuilder()
                        .setBaseUri(RestAssured.baseURI)
                        .setContentType(ContentType.JSON)
                        .setAccept(ContentType.JSON)
                        .build();


        // --------------------------------------------------------
        // Response Specification
        // --------------------------------------------------------

        responseSpec =
                new ResponseSpecBuilder()
                        .expectContentType(ContentType.JSON)
                        .expectStatusCode(200)
                        .build();
    }


    // ============================================================
    // 2. GET REQUEST
    // ============================================================

    @Test
    public void getUser() {

        Response response =
                given()
                        .spec(requestSpec)
                        .when()
                        .get("/users/1")
                        .then()
                        .spec(responseSpec)
                        .extract()
                        .response();
        String strResp=response.asString();
        System.out.println(strResp);
        // Print complete response
        response.prettyPrint();


        // ========================================================
        // JSON PATH
        // ========================================================

        JsonPath jsonPath = response.jsonPath();
        //JsonPath jsonPath1 =new JsonPath(strResp);

        /*
         * Actual response looks similar to:
         *
         * {
         *   "id": 1,
         *   "name": "Leanne Graham",
         *   "username": "Bret",
         *   "email": "Sincere@april.biz",
         *   "address": {
         *       "street": "Kulas Light",
         *       "suite": "Apt. 556",
         *       "city": "Gwenborough",
         *       "zipcode": "92998-3874",
         *       "geo": {
         *           "lat": "-37.3159",
         *           "lng": "81.1496"
         *       }
         *   },
         *   "phone": "...",
         *   "website": "...",
         *   "company": {
         *       "name": "Romaguera-Crona"
         *   }
         * }
         */


        String name =
                jsonPath.getString("name");

        String username =
                jsonPath.getString("username");

        String email =
                jsonPath.getString("email");

        String city =
                jsonPath.getString("address.city");

        String state =
                jsonPath.getString("address.geo.lat");

        String company =
                jsonPath.getString("company.name");


        System.out.println("Name       : " + name);
        System.out.println("Username   : " + username);
        System.out.println("Email      : " + email);
        System.out.println("City       : " + city);
        System.out.println("Geo Lat    : " + state);
        System.out.println("Company    : " + company);
    }


    // ============================================================
    // 3. GET + HAMCREST ASSERTIONS
    // ============================================================

    @Test
    public void getUserWithHamcrestAssertions() {

        given()
                .spec(requestSpec)

                .when()
                .get("/users/1")

                .then()
                .spec(responseSpec)

                .statusCode(200)

                .body("id", equalTo(1))

                .body(
                        "name",
                        equalTo("Leanne Graham")
                )

                .body(
                        "username",
                        equalTo("Bret")
                )

                .body(
                        "email",
                        equalTo("Sincere@april.biz")
                )

                .body(
                        "address.city",
                        equalTo("Gwenborough")
                )

                .body(
                        "company.name",
                        equalTo("Romaguera-Crona")
                );
    }


    // ============================================================
    // 4. GET ALL USERS
    // ============================================================

    @Test
    public void getAllUsers() {

        Response response =
                given()
                        .spec(requestSpec)

                        .when()
                        .get("/users")

                        .then()
                        .statusCode(200)
                        .extract()
                        .response();


        response.prettyPrint();


        // JSON Path for array

        String firstUserName =
                response
                        .jsonPath()
                        .getString("[0].name");


        System.out.println(
                "First User = " + firstUserName
        );


        // Number of users

        int userCount =
                response
                        .jsonPath()
                        .getList("$")
                        .size();


        System.out.println(
                "User Count = " + userCount
        );
    }


    // ============================================================
    // 5. POST REQUEST USING JSON PAYLOAD
    // ============================================================

    @Test
    public void createUserUsingJson() {

        String jsonPayload =
                """
                {
                    "name": "Rahul Pandey",
                    "username": "rahulpandey",
                    "email": "rahul@example.com",
                    "address": {
                        "street": "MG Road",
                        "suite": "101",
                        "city": "Bangalore",
                        "zipcode": "560001",
                        "geo": {
                            "lat": "12.9716",
                            "lng": "77.5946"
                        }
                    },
                    "phone": "9876543210",
                    "website": "rahul.com",
                    "company": {
                        "name": "Juniper",
                        "catchPhrase": "Technology Solutions",
                        "bs": "automation testing"
                    }
                }
                """;


        Response response =
                given()
                        .spec(requestSpec)
                        .body(jsonPayload)

                        .when()
                        .post("/users")

                        .then()
                        .statusCode(201)
                        .body("name", equalTo("Rahul Pandey"))
                        .body("username", equalTo("rahulpandey"))
                        .body(
                                "address.city",
                                equalTo("Bangalore")
                        )
                        .body(
                                "company.name",
                                equalTo("Juniper")
                        )
                        .extract()
                        .response();


        response.prettyPrint();


        // Extract generated ID

        Integer id =
                response
                        .jsonPath()
                        .getInt("id");


        System.out.println(
                "Created User ID = " + id
        );
    }


    // ============================================================
    // 6. POST USING POJO
    // SERIALIZATION
    // ============================================================

    @Test
    public void createUserUsingPojo() {

        User user = new User();


        user.setName("Rahul Pandey");

        user.setUsername("rahulpandey");

        user.setEmail("rahul@example.com");

        user.setPhone("9876543210");

        user.setWebsite("rahul.com");


        User.Address address = new User.Address();

        address.setStreet("MG Road");

        address.setSuite("101");

        address.setCity("Bangalore");

        address.setZipcode("560001");


        User.Geo geo = new User.Geo();

        geo.setLat("12.9716");

        geo.setLng("77.5946");

        address.setGeo(geo);


        user.setAddress(address);


        User.Company company = new User.Company();

        company.setName("Juniper");

        company.setCatchPhrase(
                "Technology Solutions"
        );

        company.setBs(
                "automation testing"
        );

        user.setCompany(company);


        /*
         * POJO -> JSON
         *
         * This is SERIALIZATION.
         */


        Response response =
                given()
                        .spec(requestSpec)
                        .body(user)

                        .when()
                        .post("/users")

                        .then()
                        .statusCode(201)
                        .body(
                                "name",
                                equalTo("Rahul Pandey")
                        )
                        .body(
                                "address.city",
                                equalTo("Bangalore")
                        )
                        .extract()
                        .response();


        response.prettyPrint();
    }


    // ============================================================
    // 7. DESERIALIZATION
    // JSON -> POJO
    // ============================================================

    @Test
    public void deserializeResponse() {

        Response response =
                given()
                        .spec(requestSpec)

                        .when()
                        .get("/users/1")

                        .then()
                        .statusCode(200)
                        .extract()
                        .response();


        /*
         * JSON -> Java POJO
         *
         * This is DESERIALIZATION.
         */


        User user =
                response.as(User.class);


        System.out.println(
                "Name = " + user.getName()
        );

        System.out.println(
                "Username = " + user.getUsername()
        );

        System.out.println(
                "Email = " + user.getEmail()
        );

        System.out.println(
                "City = " +
                        user.getAddress().getCity()
        );

        System.out.println(
                "Company = " +
                        user.getCompany().getName()
        );
    }


    // ============================================================
    // 8. PUT REQUEST
    // ============================================================

    @Test
    public void updateUserUsingPut() {

        String payload =
                """
                {
                    "id": 1,
                    "name": "Rahul Pandey Updated",
                    "username": "rahulpandey",
                    "email": "rahul.updated@example.com",
                    "phone": "9876543210",
                    "website": "rahul.com",
                    "address": {
                        "street": "MG Road",
                        "suite": "202",
                        "city": "Bangalore",
                        "zipcode": "560001"
                    },
                    "company": {
                        "name": "Nutanix"
                    }
                }
                """;


        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(payload)

                .when()
                .put("/users/{id}")

                .then()
                .statusCode(200)

                .body(
                        "id",
                        equalTo(1)
                )

                .body(
                        "name",
                        equalTo(
                                "Rahul Pandey Updated"
                        )
                )

                .body(
                        "company.name",
                        equalTo("Nutanix")
                )

                .log()
                .all();
    }


    // ============================================================
    // 9. PATCH REQUEST
    // ============================================================

    @Test
    public void updateUserUsingPatch() {

        String payload =
                """
                {
                    "company": {
                        "name": "Google"
                    }
                }
                """;


        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(payload)

                .when()
                .patch("/users/{id}")

                .then()
                .statusCode(200)

                .body(
                        "company.name",
                        equalTo("Google")
                )

                .log()
                .all();
    }


    // ============================================================
    // 10. QUERY PARAMETER
    // ============================================================

    @Test
    public void queryParameterExample() {

        Response response =
                given()
                        .spec(requestSpec)

                        .queryParam(
                                "username",
                                "Bret"
                        )

                        .when()
                        .get("/users")

                        .then()
                        .statusCode(200)
                        .extract()
                        .response();


        response.prettyPrint();
    }


    // ============================================================
    // 11. PATH PARAMETER
    // ============================================================

    @Test
    public void pathParameterExample() {

        int userId = 1;


        given()
                .spec(requestSpec)

                .pathParam(
                        "id",
                        userId
                )

                .when()
                .get("/users/{id}")

                .then()
                .statusCode(200)

                .body(
                        "id",
                        equalTo(userId)
                );
    }


    // ============================================================
    // 12. HEADERS
    // ============================================================

    @Test
    public void headerExample() {

        given()
                .header(
                        "X-Test",
                        "RestAssured"
                )

                .header(
                        "X-Tester",
                        "Rahul"
                )

                .contentType(
                        ContentType.JSON
                )

                .accept(
                        ContentType.JSON
                )

                .when()
                .get("/users/1")

                .then()
                .statusCode(200);
    }


    // ============================================================
    // 13. RESPONSE ASSERTIONS
    // ============================================================

    @Test
    public void responseAssertions() {

        given()
                .spec(requestSpec)

                .when()
                .get("/users/1")

                .then()
                .statusCode(200)

                // Body exists
                .body(
                        "id",
                        notNullValue()
                )

                // Name exists
                .body(
                        "name",
                        notNullValue()
                )

                // Email contains @
                .body(
                        "email",
                        containsString("@")
                )

                // Nested object
                .body(
                        "address",
                        notNullValue()
                )

                .body(
                        "address.city",
                        equalTo("Gwenborough")
                );
    }


    // ============================================================
    // 14. RESPONSE EXTRACTION
    // ============================================================

    @Test
    public void extractResponse() {

        Response response =
                given()
                        .spec(requestSpec)

                        .when()
                        .get("/users/1")

                        .then()
                        .statusCode(200)
                        .extract()
                        .response();


        String responseBody =
                response.asString();


        int statusCode =
                response.statusCode();


        String contentType =
                response.contentType();


        System.out.println(
                "Response Body = "
                        + responseBody
        );

        System.out.println(
                "Status Code = "
                        + statusCode
        );

        System.out.println(
                "Content Type = "
                        + contentType
        );
    }


    // ============================================================
    // 15. LOG REQUEST / RESPONSE
    // ============================================================

    @Test
    public void loggingExample() {

        given()
                .spec(requestSpec)
                .log()
                .all()

                .when()
                .get("/users/1")

                .then()
                .log()
                .all()
                .statusCode(200);
    }


    // ============================================================
    // 16. COOKIE
    // ============================================================

    @Test
    public void cookieExample() {

        given()
                .cookie(
                        "sessionId",
                        "12345"
                )

                .when()
                .get("/users/1")

                .then()
                .statusCode(200);
    }

    // ============================================================
    // 17. BASIC AUTHENTICATION (HTTPBIN)
    // ============================================================

    @Test
    public void basicAuthenticationExample() {
        given()
                .auth().preemptive().basic("user", "passwd")
                .when()
                .get("https://httpbin.org/basic-auth/user/passwd")
                .then()
                .statusCode(200)
                .body("authenticated", equalTo(true))
                .body("user", equalTo("user"));
    }

    // Challenge-based Basic Auth: the client waits for the server challenge.
    // Preemptive Basic Auth sends the Authorization header immediately.
    @Test
    public void challengeBasedBasicAuthenticationExample() {
        given()
                .auth().basic("user", "passwd")
                .when()
                .get("https://httpbin.org/basic-auth/user/passwd")
                .then()
                .statusCode(200)
                .body("authenticated", equalTo(true));
    }


    // ============================================================
    // 18-19. REAL OAUTH 2.0 CLIENT CREDENTIALS + BEARER TOKEN
    // Example provider: Spotify Web API
    //
    // Setup:
    // 1) Create an app at https://developer.spotify.com/dashboard
    // 2) Set environment variables:
    //    SPOTIFY_CLIENT_ID
    //    SPOTIFY_CLIENT_SECRET
    // 3) Never commit these credentials to Git.
    //
    // This requests a genuine short-lived access token from Spotify,
    // extracts it from the response, then uses it against a protected API.
    // Client Credentials is for app-only access; it does not access user data.
    // ============================================================

    private String getSpotifyAccessToken() {
        String clientId = System.getenv("SPOTIFY_CLIENT_ID");
        String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
        System.out.println("SPOTIFY_CLIENT_ID->"+clientId+" SPOTIFY_CLIENT_SECRET->"+clientSecret);
        if (clientId == null || clientId.isBlank()
                || clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException(
                    "Set SPOTIFY_CLIENT_ID and SPOTIFY_CLIENT_SECRET environment variables.");
        }

        Response tokenResponse =
                given()
                        .baseUri("https://accounts.spotify.com")
                        .auth().preemptive().basic(clientId, clientSecret)
                        .contentType(ContentType.URLENC)
                        .formParam("grant_type", "client_credentials")
                        .when()
                        .post("/api/token")
                        .then()
                        .statusCode(200)
                        .body("access_token", not(isEmptyOrNullString()))
                        .body("token_type", equalTo("Bearer"))
                        .extract()
                        .response();

        // The token is real, short-lived, and should not be printed in logs.
        return tokenResponse.jsonPath().getString("access_token");
    }

    @Test
    public void oauth2ClientCredentialsFlowExample() {
        String accessToken = getSpotifyAccessToken();

        // Use the real token to call a protected Spotify Web API endpoint.
        given()
                .baseUri("https://api.spotify.com")
                .auth().oauth2(accessToken)
                .queryParam("q", "Daft Punk")
                .queryParam("type", "track")
                .queryParam("limit", 1)
                .when()
                .get("/v1/search")
                .then()
                .statusCode(200)
                .body("tracks.items.size()", greaterThan(0))
                .body("tracks.items[0].name", not(isEmptyOrNullString()));
    }

    @Test
    public void bearerTokenUsageExampleWithRealOAuthToken() {
        // Demonstrates the distinction: first obtain a token, then send it as Bearer.
        String accessToken = getSpotifyAccessToken();
        System.out.println("access Token :"+accessToken);
//        given()
//                .baseUri("https://api.spotify.com")
//                .header("Authorization", "Bearer " + accessToken)
//                .queryParam("q", "Nirvana")
//                .queryParam("type", "artist")
//                .queryParam("limit", 1)
//                .when()
//                .get("/v1/search")
//                .then()
//                .statusCode(200)
//                .body("artists.items.size()", greaterThan(0));
    }


    // ============================================================
    // 20. API KEY AUTHENTICATION
    // ============================================================

    @Test
    public void apiKeyInHeaderExample() {

        String apiKey = "demo-key-123";

        given()
                .header("X-API-Key", apiKey)
                .when()
                .get("https://httpbin.org/anything")
                .then()
                .statusCode(200)
                .body("headers.X-Api-Key", equalTo(apiKey));
    }

    @Test
    public void apiKeyInQueryParameterExample() {

        String apiKey = "demo-key-123";

        given()
                .queryParam("api_key", apiKey)
                .when()
                .get("https://httpbin.org/anything")
                .then()
                .statusCode(200)
                .body("args.api_key", equalTo(apiKey));
    }


    // ============================================================
    // 21. DELETE REQUEST
    // ============================================================

    @Test
    public void deleteUser() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .when()
                .delete("/users/{id}")
                .then()
                .statusCode(200);
        // JSONPlaceholder simulates deletion; it does not persist changes.
    }


    // ============================================================
    // 22. FORM PARAMS
    // ============================================================

    @Test
    public void formParameterExample() {
        given()
                .contentType(ContentType.URLENC)
                .formParam("username", "rahul")
                .formParam("role", "sdet")
                .when()
                .post("https://httpbin.org/post")
                .then()
                .statusCode(200)
                .body("form.username", equalTo("rahul"))
                .body("form.role", equalTo("sdet"));
    }


    // ============================================================
    // 23. RESPONSE TIME / PERFORMANCE CHECK
    // ============================================================

    @Test
    public void responseTimeExample() {
        given()
                .spec(requestSpec)
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .time(lessThan(5L), TimeUnit.SECONDS);
        // This is a basic threshold assertion, not a substitute for load testing.
    }


    // ============================================================
    // 24. NEGATIVE TEST: RESOURCE NOT FOUND
    // ============================================================

    @Test
    public void userNotFoundExample() {
        given()
                .spec(requestSpec)
                .when()
                .get("/users/999999")
                .then()
                .statusCode(404);
    }


    // ============================================================
    // 25. ARRAY / COLLECTION ASSERTIONS
    // ============================================================

    @Test
    public void collectionAssertionsExample() {
        given()
                .spec(requestSpec)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("size()", equalTo(10))
                .body("id", hasItems(1, 2, 3))
                .body("name", everyItem(not(isEmptyOrNullString())));
    }


    // ============================================================
    // 26. CONTENT TYPE AND RESPONSE HEADERS
    // ============================================================

    @Test
    public void responseHeaderAndContentTypeExample() {
        given()
                .spec(requestSpec)
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .header("Content-Type", containsString("application/json"));
    }


    // ============================================================
    // 27. REQUEST-SPECIFIC OVERRIDE
    // ============================================================

    @Test
    public void overrideHeaderFromRequestSpecification() {
        given()
                .spec(requestSpec)
                .header("X-Test", "request-specific-value")
                .queryParam("_limit", 2)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));
    }


    // ============================================================
    // 28. COMMON INTERVIEW CHEAT SHEET
    // ============================================================

    /*
     * Authentication syntax:
     * Basic Auth       : auth().preemptive().basic(username, password)
     * Challenged Basic : auth().basic(username, password)
     * Bearer/OAuth2    : obtain a real access token, then auth().oauth2(accessToken)
     * OAuth token flow : POST token endpoint -> extract access_token -> call protected API
     * API key header   : header("X-API-Key", apiKey) using provider-issued key
     * API key query    : queryParam("api_key", apiKey) only if provider supports it
     *
     * Request data:
     * Path param       : pathParam("id", 1) + "/users/{id}"
     * Query param      : queryParam("username", "Bret")
     * Form data        : formParam("username", "rahul")
     * JSON body        : body(jsonString)
     * POJO body        : body(user)
     *
     * Core concepts:
     * Serialization    : Java POJO -> JSON request body
     * Deserialization  : JSON response -> Java POJO
     * Request spec     : reusable base URI, headers, content type, etc.
     * Response spec    : reusable status/header/body expectations
     * OAuth2 note      : obtaining an access token and using it are separate steps.
     */

}