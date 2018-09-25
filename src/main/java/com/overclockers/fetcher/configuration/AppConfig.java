package com.overclockers.fetcher.configuration;

import com.overclockers.fetcher.service.FetchingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.overclockers.fetcher"})
public class AppConfig {

    @Bean
    public FetchingService fetchingService(){
        return new FetchingService();
    }

}
