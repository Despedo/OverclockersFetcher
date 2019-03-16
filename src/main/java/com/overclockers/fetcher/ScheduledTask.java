package com.overclockers.fetcher;

import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.service.impl.OverclockersFetchingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class ScheduledTask {

    @Autowired
    OverclockersFetchingService overclockersFetchingService;
    @Autowired
    MailService mailService;

    //     By the default the top of every hour of every day.
//    @Scheduled(cron = "${processing.cron:0 0 * * * *}")
    @Scheduled(fixedRate = 20000)
    public void processTopics() throws InterruptedException {
        log.info("Scheduled launching of the fetching service");
        overclockersFetchingService.saveTopics();
        log.info("Scheduled launching of the mail service");
        mailService.prepareAndSendSearchResults();
    }

}
