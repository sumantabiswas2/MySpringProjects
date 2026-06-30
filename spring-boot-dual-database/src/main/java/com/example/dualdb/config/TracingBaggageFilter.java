package com.example.dualdb.config;

import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class TracingBaggageFilter {

    private final Tracer tracer;

    @Bean
    FilterRegistrationBean<OncePerRequestFilter> tracingBaggageFilterRegistration(
            @Value("${app.tracing.baggage.header:X-Request-Id}") String baggageHeader,
            @Value("${app.tracing.baggage.field:request.id}") String baggageField) {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                String requestId = (String) request.getAttribute(TracingConfig.REQUEST_ID_ATTRIBUTE);
                if (requestId == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                response.setHeader(baggageHeader, requestId);

                try (BaggageInScope scope = tracer.createBaggageInScope(baggageField, requestId)) {
                    filterChain.doFilter(request, response);
                }
            }
        });
        // Run after ServerHttpObservationFilter so the HTTP span exists and ObservationFilter can tag it
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return registration;
    }
}
