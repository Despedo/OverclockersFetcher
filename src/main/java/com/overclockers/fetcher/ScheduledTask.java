package com.overclockers.fetcher;

import com.overclockers.fetcher.service.FetchingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ScheduledTask {

    @Autowired
    FetchingService fetchingService;

    @Scheduled(fixedRateString = "${fixed.Rate:20000}")
    public void fetchTopics() {
        log.info("Start saving topics, {}", LocalDateTime.now());
        fetchingService.saveTopics();
    }

}
