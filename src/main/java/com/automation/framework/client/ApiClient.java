package com.automation.framework.client;

import com.automation.framework.auth.AuthProvider;
import com.automation.framework.utils.ProtoJsonUtils;
import com.google.protobuf.MessageOrBuilder;
import io.qameta.allure.Step;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

/**
 * Thin, fluent wrapper over REST Assured. Use {@link #builder(RequestSpecification)}
 * to construct a request; auth can be injected with {@link Builder#withAuth(AuthProvider)}.
 *
 * <p>Thread-safe: each builder produces a fresh per-thread {@link RequestSpecification}
 * derived from the shared base spec, so it is safe for {@code parallel="methods"}.
 */
public final class ApiClient {

    private static final Logger LOG = LogManager.getLogger(ApiClient.class);

    private ApiClient() {}

    public static Builder builder(RequestSpecification baseSpec) {
        return new Builder(baseSpec);
    }

    public static final class Builder {
        private final RequestSpecification spec;
        private final Map<String, Object> pathParams = new HashMap<>();
        private final Map<String, Object> queryParams = new HashMap<>();
        private final Map<String, String> headers = new HashMap<>();
        private Object body;
        private String contentType = "application/json";

        private Builder(RequestSpecification baseSpec) {
            this.spec = given().spec(baseSpec);
        }

        public Builder withAuth(AuthProvider provider) {
            provider.apply(spec);
            return this;
        }

        public Builder pathParam(String name, Object value) {
            pathParams.put(name, value);
            return this;
        }

        public Builder queryParam(String name, Object value) {
            queryParams.put(name, value);
            return this;
        }

        public Builder header(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder body(Object body) {
            // Protobuf messages are serialized via JsonFormat to honour proto3
            // wire-format semantics (e.g. snake/camel mapping, default omission rules).
            // All other payloads are handed to REST Assured's default mapper (Jackson).
            this.body = (body instanceof MessageOrBuilder msg) ? ProtoJsonUtils.toJson(msg) : body;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        @Step("GET {path}")
        public Response get(String path) { return execute(Method.GET, path); }

        @Step("POST {path}")
        public Response post(String path) { return execute(Method.POST, path); }

        @Step("PUT {path}")
        public Response put(String path) { return execute(Method.PUT, path); }

        @Step("PATCH {path}")
        public Response patch(String path) { return execute(Method.PATCH, path); }

        @Step("DELETE {path}")
        public Response delete(String path) { return execute(Method.DELETE, path); }

        private Response execute(Method method, String path) {
            spec.contentType(contentType);
            if (!headers.isEmpty())     spec.headers(headers);
            if (!queryParams.isEmpty()) spec.queryParams(queryParams);
            if (!pathParams.isEmpty())  spec.pathParams(pathParams);
            if (body != null)           spec.body(body);
            LOG.info("Dispatching {} {}", method, path);
            return spec.request(method, path).then().extract().response();
        }
    }
}
