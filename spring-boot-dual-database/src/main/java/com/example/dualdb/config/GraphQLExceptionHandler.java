package com.example.dualdb.config;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        // This is the correct method signature for newer versions
        // Override this method instead of the deprecated one
        
        if (ex instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) ex, env);
        }
        
        if (ex instanceof MethodArgumentNotValidException) {
            return handleMethodArgumentNotValid((MethodArgumentNotValidException) ex, env);
        }
        
        if (ex instanceof IllegalArgumentException) {
            return handleIllegalArgument((IllegalArgumentException) ex, env);
        }
        
        if (ex instanceof RuntimeException) {
            return handleRuntimeException((RuntimeException) ex, env);
        }
        
        // Default error handling
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();
    }

    private GraphQLError handleConstraintViolation(ConstraintViolationException ex, DataFetchingEnvironment env) {
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("validationErrors", ex.getConstraintViolations().stream()
                .map(violation -> Map.of(
                        "field", violation.getPropertyPath().toString(),
                        "message", violation.getMessage()
                ))
                .collect(Collectors.toList()));
        
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message("Validation failed: " + message)
                .extensions(extensions)
                .build();
    }

    private GraphQLError handleMethodArgumentNotValid(MethodArgumentNotValidException ex, DataFetchingEnvironment env) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("validationErrors", ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()
                ))
                .collect(Collectors.toList()));
        
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message("Validation failed: " + message)
                .extensions(extensions)
                .build();
    }

    private GraphQLError handleIllegalArgument(IllegalArgumentException ex, DataFetchingEnvironment env) {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("errorType", "IllegalArgument");
        extensions.put("details", ex.getMessage());
        
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .extensions(extensions)
                .build();
    }

    private GraphQLError handleRuntimeException(RuntimeException ex, DataFetchingEnvironment env) {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("errorType", "RuntimeException");
        extensions.put("details", ex.getMessage());
        
        // Check if it's a known business exception
        if (ex.getMessage().contains("not found")) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.NOT_FOUND)
                    .message(ex.getMessage())
                    .extensions(extensions)
                    .build();
        }
        
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("An error occurred: " + ex.getMessage())
                .extensions(extensions)
                .build();
    }
}