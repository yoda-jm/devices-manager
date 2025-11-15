package com.jss.devicemanager.registration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jss.devicemanager.common.security.RequireApiKey;
import com.jss.devicemanager.registration.model.RegisterDevice400Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.method.HandlerMethod;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyInterceptorTest {

    private static final String API_KEY_HEADER = "X-Device-Registration-API-Key";
    private static final String EXPECTED_API_KEY = "test-api-key-12345";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HandlerMethod handlerMethod;

    private ApiKeyInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new ApiKeyInterceptor(objectMapper);
        ReflectionTestUtils.setField(interceptor, "expectedApiKey", EXPECTED_API_KEY);
    }

    @Test
    void preHandle_whenHandlerIsNotHandlerMethod_shouldReturnTrue() throws Exception {
        // Given
        Object handler = new Object(); // Not a HandlerMethod

        // When
        boolean result = interceptor.preHandle(request, response, handler);

        // Then
        assertTrue(result);
        verifyNoInteractions(request, response);
    }

    @Test
    void preHandle_whenMethodDoesNotRequireApiKey_shouldReturnTrue() throws Exception {
        // Given
        when(handlerMethod.getMethodAnnotation(RequireApiKey.class)).thenReturn(null);
        when(handlerMethod.getBeanType()).thenReturn((Class) TestControllerWithoutAnnotation.class);

        // When
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Then
        assertTrue(result);
        verifyNoInteractions(request, response);
    }

    @Test
    void preHandle_whenApiKeyIsMissing_shouldReturn401() throws Exception {
        // Given
        when(handlerMethod.getMethodAnnotation(RequireApiKey.class)).thenReturn(mock(RequireApiKey.class));
        when(request.getHeader(API_KEY_HEADER)).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/Device/register");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        when(objectMapper.writeValueAsString(any(RegisterDevice400Response.class))).thenReturn("{}");

        ArgumentCaptor<RegisterDevice400Response> errorCaptor = ArgumentCaptor.forClass(RegisterDevice400Response.class);

        // When
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(objectMapper).writeValueAsString(errorCaptor.capture());

        RegisterDevice400Response capturedError = errorCaptor.getValue();
        assertEquals("Unauthorized", capturedError.getError());
        assertEquals("API key is required", capturedError.getMessage());
    }

    @Test
    void preHandle_whenApiKeyIsEmpty_shouldReturn401() throws Exception {
        // Given
        when(handlerMethod.getMethodAnnotation(RequireApiKey.class)).thenReturn(mock(RequireApiKey.class));
        when(request.getHeader(API_KEY_HEADER)).thenReturn("");
        when(request.getRequestURI()).thenReturn("/Device/register");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        when(objectMapper.writeValueAsString(any(RegisterDevice400Response.class))).thenReturn("{}");

        ArgumentCaptor<RegisterDevice400Response> errorCaptor = ArgumentCaptor.forClass(RegisterDevice400Response.class);

        // When
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(objectMapper).writeValueAsString(errorCaptor.capture());

        RegisterDevice400Response capturedError = errorCaptor.getValue();
        assertEquals("Unauthorized", capturedError.getError());
        assertEquals("API key is required", capturedError.getMessage());
    }

    @Test
    void preHandle_whenApiKeyIsInvalid_shouldReturn403() throws Exception {
        // Given
        when(handlerMethod.getMethodAnnotation(RequireApiKey.class)).thenReturn(mock(RequireApiKey.class));
        when(request.getHeader(API_KEY_HEADER)).thenReturn("wrong-api-key");
        when(request.getRequestURI()).thenReturn("/Device/register");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        when(objectMapper.writeValueAsString(any(RegisterDevice400Response.class))).thenReturn("{}");

        ArgumentCaptor<RegisterDevice400Response> errorCaptor = ArgumentCaptor.forClass(RegisterDevice400Response.class);

        // When
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");
        verify(objectMapper).writeValueAsString(errorCaptor.capture());

        RegisterDevice400Response capturedError = errorCaptor.getValue();
        assertEquals("Forbidden", capturedError.getError());
        assertEquals("Invalid API key", capturedError.getMessage());
    }

    @Test
    void preHandle_whenApiKeyIsValid_shouldReturnTrue() throws Exception {
        // Given
        when(handlerMethod.getMethodAnnotation(RequireApiKey.class)).thenReturn(mock(RequireApiKey.class));
        when(request.getHeader(API_KEY_HEADER)).thenReturn(EXPECTED_API_KEY);

        // When
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Then
        assertTrue(result);
        verifyNoInteractions(response);
        verify(objectMapper, never()).writeValueAsString(any());
    }

    @Test
    void preHandle_whenClassHasRequireApiKeyAnnotation_shouldValidateApiKey() throws Exception {
        // Given
        when(handlerMethod.getMethodAnnotation(RequireApiKey.class)).thenReturn(null);
        when(handlerMethod.getBeanType()).thenReturn((Class) TestControllerWithAnnotation.class);
        when(request.getHeader(API_KEY_HEADER)).thenReturn(EXPECTED_API_KEY);

        // When
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Then
        assertTrue(result);
        verify(request).getHeader(API_KEY_HEADER);
    }

    @Test
    void preHandle_whenClassHasRequireApiKeyButInvalidKey_shouldReturn403() throws Exception {
        // Given
        when(handlerMethod.getMethodAnnotation(RequireApiKey.class)).thenReturn(null);
        when(handlerMethod.getBeanType()).thenReturn((Class) TestControllerWithAnnotation.class);
        when(request.getHeader(API_KEY_HEADER)).thenReturn("invalid-key");
        when(request.getRequestURI()).thenReturn("/Device/register");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        when(objectMapper.writeValueAsString(any(RegisterDevice400Response.class))).thenReturn("{}");

        ArgumentCaptor<RegisterDevice400Response> errorCaptor = ArgumentCaptor.forClass(RegisterDevice400Response.class);

        // When
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");
        verify(objectMapper).writeValueAsString(errorCaptor.capture());

        RegisterDevice400Response capturedError = errorCaptor.getValue();
        assertEquals("Forbidden", capturedError.getError());
        assertEquals("Invalid API key", capturedError.getMessage());
    }

    // Helper classes for testing
    private static class TestControllerWithoutAnnotation {
        // Empty method used only for testing HandlerMethod detection
        public void someMethod() {
            // No implementation needed - used only to create HandlerMethod instances for testing
        }
    }

    @RequireApiKey
    private static class TestControllerWithAnnotation {
        // Empty method used only for testing HandlerMethod detection
        public void someMethod() {
            // No implementation needed - used only to create HandlerMethod instances for testing
        }
    }
}
