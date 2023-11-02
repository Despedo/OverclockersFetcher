package com.overclockers.fetcher.configuration;

import com.overclockers.fetcher.mail.impl.JavaMailService;
import com.overclockers.fetcher.mail.impl.SimpleJavaMailService;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;

@Configuration
@EnableTransactionManagement
@EntityScan("com.overclockers.fetcher.entity")
@EnableAsync(proxyTargetClass = true)
@EnableJpaRepositories("com.overclockers.fetcher.repository")
public class AppConfig implements WebMvcConfigurer {

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(25);
        executor.setQueueCapacity(30);
        return executor;
    }

    @Bean
    public SimpleJavaMailService.SimpleJavaMailServiceBuilder<?, ?> simpleJavaMailServiceBuilder() {
        return SimpleJavaMailService.builder();
    }

    @Bean
    public JavaMailService.JavaMailServiceBuilder<?, ?> javaMailServiceBuilder() {
        return JavaMailService.builder();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

}
