package com.automation.framework.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Auto-attaches {@link RetryAnalyzer} to every {@code @Test} method,
 * so individual tests don't need to declare {@code retryAnalyzer = ...}.
 */
public class RetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        Class<?> existing = annotation.getRetryAnalyzerClass();
        if (existing == null || "DisableRetryAnalyzer".equals(existing.getSimpleName())) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
