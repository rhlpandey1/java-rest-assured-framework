package com.automation.framework.auth;

import com.automation.framework.config.ConfigManager;

import java.util.Locale;

/**
 * Factory selecting the active {@link AuthProvider} based on {@code auth.type}.
 * Bearer is the default; OAuth2 and API key are pluggable.
 */
public final class AuthProviderFactory {

    private AuthProviderFactory() {}

    public static AuthProvider resolve() {
        String type = ConfigManager.getInstance().get("auth.type", "bearer").toLowerCase(Locale.ROOT);
        return switch (type) {
            case "bearer" -> new BearerTokenAuthProvider();
            case "oauth2" -> new OAuth2AuthProvider();
            case "apikey", "api-key" -> new ApiKeyAuthProvider();
            case "none", "off" -> new NoAuthProvider();
            default -> throw new IllegalArgumentException("Unknown auth.type: " + type);
        };
    }
}
