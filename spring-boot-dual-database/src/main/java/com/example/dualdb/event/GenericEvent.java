package com.example.dualdb.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.example.dualdb.event.CustomerEvent.EventType;
import com.example.dualdb.model.mysql.Customer;

import lombok.Getter;

public class GenericEvent<T> extends ApplicationEvent{
	
	private static final long serialVersionUID = 1L;
	@Getter private final T what;
    @Getter private final boolean success;

    public GenericEvent(Object source, T what, boolean success) {
        super(source);
        this.what = what;
        this.success = success;
    }

    
}
