package com.automation.framework.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries flaky tests up to {@value #MAX_RETRIES} times.
 * Hooked into every {@code @Test} method via {@link RetryListener}.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRIES = 2;

    private final ThreadLocal<Integer> attempts = ThreadLocal.withInitial(() -> 0);

    @Override
    public boolean retry(ITestResult result) {
        int current = attempts.get();
        if (current < MAX_RETRIES) {
            attempts.set(current + 1);
            LOG.warn("Retrying {} (attempt {}/{}) — cause: {}",
                    result.getName(), current + 1, MAX_RETRIES,
                    result.getThrowable() == null ? "n/a" : result.getThrowable().getMessage());
            return true;
        }
        attempts.remove();
        return false;
    }
}
