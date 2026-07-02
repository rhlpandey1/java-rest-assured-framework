package com.automation.framework.auth;

import com.automation.framework.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

/**
 * OAuth2 client-credentials flow. Caches the access token and refreshes
 * automatically when expired. Pluggable — disabled by default.
 */
public class OAuth2AuthProvider implements AuthProvider {

    private static final Logger LOG = LogManager.getLogger(OAuth2AuthProvider.class);
    private static final long SAFETY_WINDOW_SECONDS = 30L;

    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final String scope;

    private volatile String cachedToken;
    private volatile Instant expiresAt = Instant.EPOCH;

    public OAuth2AuthProvider() {
        ConfigManager cfg = ConfigManager.getInstance();
        this.tokenUrl = cfg.get("auth.oauth2.tokenUrl");
        this.clientId = cfg.get("auth.oauth2.clientId");
        this.clientSecret = cfg.get("auth.oauth2.clientSecret");
        this.scope = cfg.get("auth.oauth2.scope", "");
    }

    @Override
    public synchronized RequestSpecification apply(RequestSpecification spec) {
        if (cachedToken == null || Instant.now().isAfter(expiresAt)) {
            refresh();
        }
        return spec.header("Authorization", "Bearer " + cachedToken);
    }

    private void refresh() {
        LOG.info("Requesting new OAuth2 access token from {}", tokenUrl);
        Response resp = RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "client_credentials")
                .formParam("client_id", clientId)
                .formParam("client_secret", clientSecret)
                .formParam("scope", scope)
                .post(tokenUrl)
                .then().statusCode(200).extract().response();

        this.cachedToken = resp.jsonPath().getString("access_token");
        long expiresIn = resp.jsonPath().getLong("expires_in");
        this.expiresAt = Instant.now().plusSeconds(expiresIn - SAFETY_WINDOW_SECONDS);
    }

    @Override
    public String type() {
        return "oauth2";
    }
}
