package com.example.dualdb.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.dualdb.controller.graphql.CustomerGraphQLController;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApplicationContextEventListenerHandler {

    @EventListener(classes = { ContextStartedEvent.class, ContextStoppedEvent.class})
    public void handleContextLifecycle(ApplicationContextEvent event) {
    	
    	
    	if (event instanceof ContextStartedEvent) {
            log.debug("Application context has been manually STARTED.");
            log.debug("This event used for Trigger polling mechanisms or resume background workers here");
            
            
        } else if (event instanceof ContextStoppedEvent) {
        	log.debug("Application context has been manually STOPPED.");
        	log.debug("This event used for Pause active jobs or release temporary resource locks here");
            

        }
    }
}