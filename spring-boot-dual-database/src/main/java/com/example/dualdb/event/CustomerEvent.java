package com.example.dualdb.event;


import com.example.dualdb.model.mysql.Customer;
import com.example.dualdb.service.CustomerSyncService;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEvent;

public class CustomerEvent extends ApplicationEvent {
    
    @Getter private final Long customerId;
    @Getter private final EventType eventType;
    @Getter private final Customer customer;
    @Getter private final long timestamp1;

    public CustomerEvent(Object source, Long customerId, EventType eventType, Customer customer) {
        super(source);
        this.customerId = customerId;
        this.eventType = eventType;
        this.customer = customer;
        this.timestamp1 = System.currentTimeMillis();
    }


	public enum EventType {
        CREATED,
        UPDATED,
        DELETED,
        ACTIVATED,
        DEACTIVATED,
        SYNCED_TO_NEO4J
    }
}