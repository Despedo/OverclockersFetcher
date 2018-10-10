package com.overclockers.fetcher;

import com.overclockers.fetcher.service.OverclockersFetchingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ScheduledTask {

    @Autowired
    OverclockersFetchingService overclockersFetchingService;

    // By the default the top of every hour of every day.
    @Scheduled(cron = "${cron.exp:0 0 * * * *}")
    public void fetchTopics() {
        log.info("Start saving topics, {}", LocalDateTime.now());
        overclockersFetchingService.saveTopics();
    }

}
