package com.automation.tests.base;

import com.automation.framework.auth.AuthProvider;
import com.automation.framework.auth.AuthProviderFactory;
import com.automation.framework.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

/**
 * Foundation class for every test. Builds per-service {@link RequestSpecification} /
 * {@link ResponseSpecification} pairs, injects the env-resolved base URI, and applies
 * the active {@link AuthProvider}.
 *
 * <p>All spec construction is done in {@code @BeforeClass} so each thread/class gets
 * an isolated copy — safe for TestNG {@code parallel="methods"}.
 */
public abstract class BaseTest {

    private static final Logger LOG = LogManager.getLogger(BaseTest.class);

    protected ConfigManager   config;
    protected AuthProvider    authProvider;
    protected RequestSpecification  requestSpec;
    protected ResponseSpecification responseSpec;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        ConfigManager cfg = ConfigManager.getInstance();
        LOG.info("=== Test suite starting | env={} ===", cfg.getEnvironment());
        RestAssured.urlEncodingEnabled = true;
    }

    @BeforeClass(alwaysRun = true)
    public void classSetup() {
        this.config       = ConfigManager.getInstance();
        this.authProvider = AuthProviderFactory.resolve();
        this.requestSpec  = buildRequestSpec(serviceBaseUriKey());
        this.responseSpec = buildResponseSpec();
    }

    /** Each test class declares which microservice's base URI it targets. */
    protected abstract String serviceBaseUriKey();

    private RequestSpecification buildRequestSpec(String baseUriKey) {
        RequestSpecBuilder b = new RequestSpecBuilder()
                .setBaseUri(config.get(baseUriKey))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured());

        if (config.getBoolean("log.requests"))  b.addFilter(new RequestLoggingFilter(LogDetail.ALL));
        if (config.getBoolean("log.responses")) b.addFilter(new ResponseLoggingFilter(LogDetail.ALL));

        return b.build();
    }

    private ResponseSpecification buildResponseSpec() {
        long maxRt = config.getInt("http.maxResponseTimeMs");
        return new ResponseSpecBuilder()
                .expectResponseTime(org.hamcrest.Matchers.lessThan(maxRt))
                .build();
    }

    /** Convenience override: provides an unauthenticated spec for 401 tests. */
    protected RequestSpecification unauthenticatedSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(config.get(serviceBaseUriKey()))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }
}
