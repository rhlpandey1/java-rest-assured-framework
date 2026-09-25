package com.automation.tests.javaVpython;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BookingCrudE2ETest {

    private String token;
    private int bookingId;

    @BeforeClass
    public void setup() {

        RestAssured.baseURI =
                "https://restful-booker.herokuapp.com";

        // Authenticate
        token =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                            {
                                "username": "admin",
                                "password": "password123"
                            }
                            """)
                        .when()
                        .post("/auth")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("token");

        System.out.println("Token: " + token);
    }

    @Test
    public void bookingCrudE2ETest() {

        // =====================================================
        // 1. POST - Create Booking
        // =====================================================

        String createBookingRequest = """
                {
                    "firstname": "Rahul",
                    "lastname": "Test",
                    "totalprice": 150,
                    "depositpaid": true,
                    "bookingdates": {
                        "checkin": "2026-10-01",
                        "checkout": "2026-10-05"
                    },
                    "additionalneeds": "Breakfast"
                }
                """;

        Response createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(createBookingRequest)
                        .when()
                        .post("/booking")
                        .then()
                        .statusCode(200)
                        .body("bookingid", notNullValue())
                        .body("booking.firstname", equalTo("Rahul"))
                        .extract()
                        .response();

        bookingId = createResponse.path("bookingid");

        System.out.println(
                "Created Booking ID: " + bookingId
        );


        // =====================================================
        // 2. GET - Retrieve Booking
        // =====================================================

        given()
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Rahul"))
                .body("lastname", equalTo("Test"))
                .body("totalprice", equalTo(150));

        System.out.println("GET validation successful");


        // =====================================================
        // 3. PUT - Replace Complete Booking
        // =====================================================

        String putRequest = """
                {
                    "firstname": "RahulUpdated",
                    "lastname": "Automation",
                    "totalprice": 250,
                    "depositpaid": false,
                    "bookingdates": {
                        "checkin": "2026-11-01",
                        "checkout": "2026-11-10"
                    },
                    "additionalneeds": "Lunch"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(putRequest)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("RahulUpdated"))
                .body("totalprice", equalTo(250));

        System.out.println("PUT validation successful");


        // =====================================================
        // 4. GET - Verify PUT
        // =====================================================

        given()
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("RahulUpdated"))
                .body("lastname", equalTo("Automation"))
                .body("totalprice", equalTo(250));


        // =====================================================
        // 5. PATCH - Partial Update
        // =====================================================

        String patchRequest = """
                {
                    "firstname": "RahulPatched"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(patchRequest)
                .when()
                .patch("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("RahulPatched"));

        System.out.println("PATCH validation successful");


        // =====================================================
        // 6. GET - Verify PATCH
        // =====================================================

        given()
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("RahulPatched"))
                .body("lastname", equalTo("Automation"));


        // =====================================================
        // 7. DELETE
        // =====================================================

        given()
                .cookie("token", token)
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201);

        System.out.println("DELETE successful");


        // =====================================================
        // 8. GET - Verify Delete
        // =====================================================

        given()
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(404);

        System.out.println("Delete validation successful");
    }
}
