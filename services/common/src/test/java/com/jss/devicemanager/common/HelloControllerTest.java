package com.jss.devicemanager.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HelloControllerTest {

    @InjectMocks
    private HelloController helloController;

    @Test
    void hello_shouldReturnGreetingWithApplicationName() {
        // Given
        String applicationName = "test-app";
        ReflectionTestUtils.setField(helloController, "applicationName", applicationName);

        // When
        String result = helloController.hello();

        // Then
        assertEquals("Hello from test-app!", result);
    }
}
