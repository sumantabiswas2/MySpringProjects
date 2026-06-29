package com.example.dualdb.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.dualdb.event.GenericEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GenerericEventListener2 {
	
	// Example - Only handle success results
	@EventListener(condition = "#event.success")
    public void handleSuccessful(GenericEvent<String> event) {
        log.debug("This will handling only SUCCESS generic event (conditional).");
    }
}
