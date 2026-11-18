
package com.automation.tests.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@PactConsumerTest
public class UserServicePactTest {

    @Pact(provider = "UserService", consumer = "UserDashboard")
    public V4Pact userDetailsPact(PactBuilder builder) {

        return builder
                .expectsToReceiveHttpInteraction(
                        "A request for user 101",
                        interaction -> interaction
                                .state("User 101 exists")
                                .withRequest(request -> request
                                        .method("GET")
                                        .path("/users/101")
                                )
                                .willRespondWith(response -> response
                                        .status(200)
                                        .header("Content-Type", "application/json")
                                        .body(
                                                LambdaDsl.newJsonBody(body -> {
                                                    body.integerType("id", 101);
                                                    body.stringType("name", "Rahul");
                                                    body.stringType("status", "ACTIVE");
                                                }).build()
                                        )
                                )
                )
                .toPact();
    }

    @Test
    @PactTestFor(
            providerName = "UserService",
            pactMethod = "userDetailsPact"
    )
    public void shouldGetUserDetails(MockServer mockServer)
            throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        mockServer.getUrl() + "/users/101"
                ))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Rahul"));
        assertTrue(response.body().contains("ACTIVE"));
    }
}
