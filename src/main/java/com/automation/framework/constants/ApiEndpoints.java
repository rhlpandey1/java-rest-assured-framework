package com.automation.framework.constants;

/** Centralised endpoint paths; keep service-scoped constants here. */
public final class ApiEndpoints {

    private ApiEndpoints() {}

    // ---- Users service ----
    public static final String USERS         = "/users";
    public static final String USER_BY_ID    = "/users/{id}";

    // ---- Auth service ----
    public static final String LOGIN         = "/auth/login";
    public static final String REFRESH_TOKEN = "/auth/refresh";

    // ---- Health ----
    public static final String HEALTH        = "/health";
}
