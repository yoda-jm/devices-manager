package com.jss.devicemanager.viewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.jss.devicemanager.viewer",
        "com.jss.devicemanager.common.startup"
})
@EntityScan(basePackages = "com.jss.devicemanager.common.entity")
@EnableJpaRepositories(basePackages = "com.jss.devicemanager.common.repository")
public class DeviceViewerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceViewerApplication.class, args);
    }
}
