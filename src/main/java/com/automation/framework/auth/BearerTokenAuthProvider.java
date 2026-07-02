package com.automation.framework.auth;

import com.automation.framework.config.ConfigManager;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Default auth strategy: static Bearer token read from {@code auth.bearer.token}.
 */
public class BearerTokenAuthProvider implements AuthProvider {

    private static final Logger LOG = LogManager.getLogger(BearerTokenAuthProvider.class);
    private final String token;

    public BearerTokenAuthProvider() {
        this(ConfigManager.getInstance().get("auth.bearer.token", ""));
    }

    public BearerTokenAuthProvider(String token) {
        this.token = token;
    }

    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        if (token == null || token.isBlank()) {
            LOG.warn("Bearer token is empty; sending request without Authorization header");
            return spec;
        }
        return spec.header("Authorization", "Bearer " + token);
    }

    @Override
    public String type() {
        return "bearer";
    }
}
