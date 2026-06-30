package com.example.dualdb.config;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;

@Configuration
public class TracingConfig {

    public static final String REQUEST_ID_ATTRIBUTE = "app.request.id";

    @Bean
    ObservationFilter requestIdObservationFilter(
            @Value("${app.tracing.baggage.field:request.id}") String baggageField) {
        return context -> {
            if (context instanceof ServerRequestObservationContext serverContext) {
                String requestId = (String) serverContext.getCarrier().getAttribute(REQUEST_ID_ATTRIBUTE);
                if (requestId != null) {
                    context.addLowCardinalityKeyValue(KeyValue.of(baggageField, requestId));
                }
            }
            return context;
        };
    }
}
