package com.automation.framework.auth;

import io.restassured.specification.RequestSpecification;

/** Null-object provider for endpoints that don't require auth (e.g. health, login). */
public class NoAuthProvider implements AuthProvider {

    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        return spec;
    }

    @Override
    public String type() {
        return "none";
    }
}
