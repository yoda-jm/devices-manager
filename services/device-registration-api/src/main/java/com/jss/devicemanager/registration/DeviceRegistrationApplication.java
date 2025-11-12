package com.jss.devicemanager.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.jss.devicemanager")
public class DeviceRegistrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceRegistrationApplication.class, args);
    }
}
