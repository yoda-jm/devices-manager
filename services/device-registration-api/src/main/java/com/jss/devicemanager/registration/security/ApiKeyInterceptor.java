package com.jss.devicemanager.registration.security;

import com.jss.devicemanager.common.security.RequireApiKey;
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
            return true;
        }

        String providedApiKey = request.getHeader(API_KEY_HEADER);

        if (providedApiKey == null || providedApiKey.isEmpty()) {
            logger.warn("API key missing in request to {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"API key is required\"}");
            return false;
        }

        if (!expectedApiKey.equals(providedApiKey)) {
            logger.warn("Invalid API key provided for request to {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Invalid API key\"}");
            return false;
        }

        logger.debug("API key validated successfully for {}", request.getRequestURI());
        return true;
    }
}
