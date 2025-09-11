package com.example.book_management;

import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    // Test configuration for H2 in-memory database
    // Uses application-test.properties for configuration
}
