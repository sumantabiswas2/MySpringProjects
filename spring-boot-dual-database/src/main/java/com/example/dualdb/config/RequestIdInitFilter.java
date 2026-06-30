package com.example.dualdb.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class RequestIdInitFilter {

    @Bean
    FilterRegistrationBean<OncePerRequestFilter> requestIdInitFilterRegistration(
            @Value("${app.tracing.baggage.header:X-Request-Id}") String baggageHeader) {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                String requestId = Optional.ofNullable(request.getHeader(baggageHeader))
                        .filter(value -> !value.isBlank())
                        .orElse(UUID.randomUUID().toString());
                request.setAttribute(TracingConfig.REQUEST_ID_ATTRIBUTE, requestId);
                filterChain.doFilter(request, response);
            }
        });
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
