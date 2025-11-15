package com.jss.devicemanager.common.startup;

import com.jss.devicemanager.common.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DeviceCountLogger implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DeviceCountLogger.class);
    private final DeviceRepository deviceRepository;
    private final String applicationName;

    public DeviceCountLogger(
            DeviceRepository deviceRepository,
            @Value("${spring.application.name:Application}") String applicationName) {
        this.deviceRepository = deviceRepository;
        this.applicationName = applicationName;
    }

    @Override
    public void run(ApplicationArguments args) {
        long deviceCount = deviceRepository.count();
        logger.info("=".repeat(80));
        logger.info("{} started successfully", applicationName);
        logger.info("Total devices in database: {}", deviceCount);
        logger.info("=".repeat(80));
    }
}
