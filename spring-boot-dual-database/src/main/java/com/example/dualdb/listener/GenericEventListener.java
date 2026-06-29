package com.example.dualdb.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.example.dualdb.event.GenericEvent;

import ch.qos.logback.classic.Logger;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GenericEventListener 
  implements ApplicationListener<GenericEvent<String>> {
	
    @Override
    public void onApplicationEvent(@NonNull GenericEvent<String> event) {
        log.debug("This will handle all generic type application events - " + event.getWhat());
    }
}