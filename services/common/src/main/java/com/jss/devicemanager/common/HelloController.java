package com.jss.devicemanager.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping("/")
    public String hello() {
        logger.debug("Hello endpoint called");
        return "Hello from " + applicationName + "!";
    }
}
