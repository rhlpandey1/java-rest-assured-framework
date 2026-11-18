package com.automation.tests.wiremock;



import com.github.tomakehurst.wiremock.WireMockServer;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class UserServiceWireMockTest {

    private static WireMockServer wireMockServer;

    private HttpClient client;

    @BeforeClass
    public void startServer() {

        wireMockServer = new WireMockServer(
                options().dynamicPort()
        );

        wireMockServer.start();
    }

    @BeforeMethod
    public void setup() {

        client = HttpClient.newHttpClient();

        wireMockServer.resetAll();
    }

    @Test
    public void shouldReturnUserDetails() throws Exception {

        // Arrange: Configure WireMock stub
        wireMockServer.stubFor(
                get(urlEqualTo("/users/101"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(
                                                "Content-Type",
                                                "application/json"
                                        )
                                        .withBody("""
                            {
                              "id": 101,
                              "name": "Rahul",
                              "status": "ACTIVE"
                            }
                            """)
                        )
        );

        // Act: Call the virtual service
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        wireMockServer.baseUrl() + "/users/101"
                ))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        // Assert
        Assert.assertEquals(response.statusCode(), 200);

        Assert.assertTrue(
                response.body().contains("Rahul")
        );

        // Verify HTTP interaction
        wireMockServer.verify(
                1,
                getRequestedFor(
                        urlEqualTo("/users/101")
                )
        );
    }

    @Test
    public void shouldReturn404WhenUserDoesNotExist()
            throws Exception {

        wireMockServer.stubFor(
                get(urlEqualTo("/users/999"))
                        .willReturn(
                                aResponse()
                                        .withStatus(404)
                                        .withHeader(
                                                "Content-Type",
                                                "application/json"
                                        )
                                        .withBody("""
                            {"message":"User not found"}
                            """)
                        )
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        wireMockServer.baseUrl() + "/users/999"
                ))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        Assert.assertEquals(response.statusCode(), 404);
        Assert.assertTrue(
                response.body().contains("User not found")
        );
    }

    @AfterClass(alwaysRun = true)
    public void stopServer() {

        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}

