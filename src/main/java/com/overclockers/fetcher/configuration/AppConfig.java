package com.overclockers.fetcher.configuration;

import com.overclockers.fetcher.service.FetchingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan({"com.overclockers.fetcher"})
@PropertySource("classpath:/forum.properties")
public class AppConfig {

    @Bean
    public FetchingService fetchingService(){
        return new FetchingService();
    }

}
