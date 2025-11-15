package com.jss.devicemanager.registration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jss.devicemanager.common.security.RequireApiKey;
import com.jss.devicemanager.registration.model.RegisterDevice400Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to validate API key from X-Device-Registration-API-Key header.
 * Only applied to controllers/methods annotated with @RequireApiKey.
 */
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyInterceptor.class);
    private static final String API_KEY_HEADER = "X-Device-Registration-API-Key";

    @Value("${device.registration.api.key}")
    private String expectedApiKey;

    private final ObjectMapper objectMapper;

    public ApiKeyInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // Check if method or class has @RequireApiKey annotation
        boolean requiresApiKey = handlerMethod.getMethodAnnotation(RequireApiKey.class) != null
                || handlerMethod.getBeanType().getAnnotation(RequireApiKey.class) != null;

        if (!requiresApiKey) {
            // API key not required, continue
            return true;
        }

        String providedApiKey = request.getHeader(API_KEY_HEADER);

        if (providedApiKey == null || providedApiKey.isEmpty()) {
            logger.warn("API key missing in request to {}", request.getRequestURI());
            RegisterDevice400Response errorResponse = new RegisterDevice400Response("Unauthorized", "API key is required");
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, errorResponse);
            return false;
        }

        if (!expectedApiKey.equals(providedApiKey)) {
            logger.warn("Invalid API key provided for request to {}", request.getRequestURI());
            RegisterDevice400Response errorResponse = new RegisterDevice400Response("Forbidden", "Invalid API key");
            writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, errorResponse);
            return false;
        }

        logger.debug("API key validated successfully for {}", request.getRequestURI());
        return true;
    }

    private void writeErrorResponse(HttpServletResponse response, int status, RegisterDevice400Response errorResponse) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
