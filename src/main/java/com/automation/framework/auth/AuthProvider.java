package com.automation.framework.auth;

import io.restassured.specification.RequestSpecification;

/**
 * Strategy interface for authentication. Implementations are pluggable
 * via {@link AuthProviderFactory} and selected by the {@code auth.type}
 * property in application-{env}.properties.
 */
public interface AuthProvider {

    /** Applies auth headers / params to the supplied spec and returns it. */
    RequestSpecification apply(RequestSpecification spec);

    /** Identifier of the strategy (e.g. {@code bearer}, {@code oauth2}, {@code apikey}). */
    String type();
}
