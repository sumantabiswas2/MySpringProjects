package com.example.dualdb.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.dualdb.controller.graphql.CustomerGraphQLController;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApplicationEventListenerHandler {

    @EventListener(classes = { ApplicationStartedEvent.class, ApplicationReadyEvent.class})
    public void handleContextLifecycle(ApplicationEvent event) {
    	
    	
    	if (event instanceof ApplicationStartedEvent) {
            log.debug("Application starting .... .");
            log.debug("This event uses to initialize events");
            // Trigger polling mechanisms or resume background workers here
            
            
        } else if (event instanceof ApplicationReadyEvent) {
        	log.debug("Application is reday .... .");
            log.debug("This event uses to Warm up caches, load data");
            // Pause active jobs or release temporary resource locks here
            
            
        }
    }
}