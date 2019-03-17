package com.overclockers.fetcher;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.service.ApplicationUserService;
import com.overclockers.fetcher.service.impl.OverclockersFetchingService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class ScheduledTask {

    private OverclockersFetchingService overclockersFetchingService;
    private ApplicationUserService applicationUserService;
    private MailService mailService;

    // Todo change schedule period
    // By the default the top of every hour of every day.
//    @Scheduled(cron = "${processing.cron:0 0 * * * *}")
    @Scheduled(fixedRate = 20000)
    public void processTopics() {
        log.info("Scheduled launching of the fetching service");
        overclockersFetchingService.fetchAndSaveTopics();
        log.info("Scheduled launching of the mail service");

        List<ApplicationUser> applicationUsers = applicationUserService.findAllEnabledUsers();
        for (ApplicationUser user : applicationUsers) {
            mailService.processUserRequestEmail(user);
        }

    }

}
