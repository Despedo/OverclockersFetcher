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

//    @Scheduled(cron = "${processing.cron}")
    @Scheduled(fixedRate = 300000)
    public void processTopics() {
        log.info("Scheduled topics processing");
        overclockersFetchingService.fetchAndSaveTopics();

        List<ApplicationUser> applicationUsers = applicationUserService.findAllEnabledUsers();
        for (ApplicationUser user : applicationUsers) {
            mailService.processUserRequestEmail(user);
        }

    }

}
