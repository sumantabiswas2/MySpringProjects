package com.example.dualdb.listener;

import com.example.dualdb.event.CustomerEvent;
import com.example.dualdb.model.mysql.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class CustomerLifecycleListener {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Async("asyncEventtaskExecutor")
    @EventListener
    public void handleCustomerCreated(CustomerEvent event) {
        if ("CREATED".equals(event.getEventType())) {
            log.info("📧 Customer CREATED event received at {}", 
                    LocalDateTime.now().format(FORMATTER));
            Customer customer = event.getCustomer();
            log.info("  - Customer: {} {}", customer.getFirstName(), customer.getLastName());
            log.info("  - Email: {}", customer.getEmail());
            log.info("  - ID: {}", customer.getCustomerId());
            
            // Perform post-creation actions
            // - Send welcome email
            // - Update analytics
            // - Sync to Neo4j (if needed)
            // - Log to audit trail
        }
    }

    @Async("asyncEventtaskExecutor")
    @EventListener
    public void handleCustomerUpdated(CustomerEvent event) {
        if ("UPDATED".equals(event.getEventType())) {
            log.info("📧 Customer UPDATED event received at {}", 
                    LocalDateTime.now().format(FORMATTER));
            Customer customer = event.getCustomer();
            log.info("  - Customer: {} {}", customer.getFirstName(), customer.getLastName());
            log.info("  - Email: {}", customer.getEmail());
            log.info("  - ID: {}", customer.getCustomerId());
            
            // Perform post-update actions
            // - Update search index
            // - Update cache
            // - Log to audit trail
        }
    }

    @Async("asyncEventtaskExecutor")
    @EventListener
    public void handleCustomerDeleted(CustomerEvent event) {
        if ("DELETED".equals(event.getEventType())) {
            log.info("📧 Customer DELETED event received at {}", 
                    LocalDateTime.now().format(FORMATTER));
            log.info("  - Customer ID: {}", event.getCustomerId());
            
            // Perform post-deletion actions
            // - Clean up related data
            // - Remove from cache
            // - Log to audit trail
        }
    }

    @Async
    @EventListener
    public void handleCustomerDeactivated(CustomerEvent event) {
        if ("DEACTIVATED".equals(event.getEventType())) {
            log.info("📧 Customer DEACTIVATED event received at {}", 
                    LocalDateTime.now().format(FORMATTER));
            Customer customer = event.getCustomer();
            log.info("  - Customer: {} {}", customer.getFirstName(), customer.getLastName());
            log.info("  - ID: {}", customer.getCustomerId());
            
            // Perform deactivation actions
            // - Invalidate sessions
            // - Remove from cache
            // - Log to audit trail
        }
    }

    @Async("asyncEventtaskExecutor")
    @EventListener
    public void handleCustomerActivated(CustomerEvent event) {
        if ("ACTIVATED".equals(event.getEventType())) {
            log.info("📧 Customer ACTIVATED event received at {}", 
                    LocalDateTime.now().format(FORMATTER));
            Customer customer = event.getCustomer();
            log.info("  - Customer: {} {}", customer.getFirstName(), customer.getLastName());
            log.info("  - ID: {}", customer.getCustomerId());
            
            // Perform activation actions
            // - Send notification
            // - Update cache
            // - Log to audit trail
        }
    }
}
