package com.automation.framework.auth;

import com.automation.framework.config.ConfigManager;
import io.restassured.specification.RequestSpecification;

/**
 * API-key auth: sends the key in a configurable header (default {@code X-API-Key}).
 */
public class ApiKeyAuthProvider implements AuthProvider {

    private final String headerName;
    private final String apiKey;

    public ApiKeyAuthProvider() {
        ConfigManager cfg = ConfigManager.getInstance();
        this.headerName = cfg.get("auth.apikey.header", "X-API-Key");
        this.apiKey = cfg.get("auth.apikey.value", "");
    }

    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        if (apiKey == null || apiKey.isBlank()) return spec;
        return spec.header(headerName, apiKey);
    }

    @Override
    public String type() {
        return "apikey";
    }
}
