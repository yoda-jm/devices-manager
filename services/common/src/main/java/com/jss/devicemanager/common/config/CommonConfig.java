package com.jss.devicemanager.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Enables Spring to discover components, JPA repositories, and entities from the common module.
 * Required because common is a separate JAR not automatically scanned by Spring Boot.
 */
@Configuration
@ComponentScan(basePackages = "com.jss.devicemanager.common")
@EnableJpaRepositories(basePackages = "com.jss.devicemanager.common.repository")
@EntityScan(basePackages = "com.jss.devicemanager.common.entity")
public class CommonConfig {
}
