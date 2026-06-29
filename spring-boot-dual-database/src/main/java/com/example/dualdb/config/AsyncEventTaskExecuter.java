package com.example.dualdb.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncEventTaskExecuter {
    // [Likely] Always configure a custom thread pool. 
    // Defaulting to SimpleAsyncTaskExecutor spawns unbounded threads.
    @Bean(name = "asyncEventtaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("AsyncEvent-");
        executor.initialize();
     // If the queue and pool are maxed out, force the main publishing thread 
     // to execute the event handler itself, temporarily slowing down incoming traffic.
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
    
    /**
     * The Risk: If your application receives a sudden spike of traffic and throws 36 concurrent events at this listener, the system behavior breaks.

		5 events occupy the core threads.
		
		25 events fill the queue completely (Total: 30).
		
		5 events trigger the expansion up to the max pool size (Total: 35).
		
		The 36th event causes a RejectedExecutionException. By default, Spring will crash the main publishing thread with this exception, meaning your user's web request will fail completely because a background event task overflowed.
		
		What to do instead:
		You should explicitly define a rejection policy using setRejectedExecutionHandler. For example, use the CallerRunsPolicy to gracefully degrade performance instead of crashing:
     */
}