package com.overclockers.fetcher;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.service.ApplicationUserService;
import com.overclockers.fetcher.service.impl.OverclockersFetchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
class ScheduledTaskTest {

    @Mock
    private OverclockersFetchingService overclockersFetchingService;
    @Mock
    private ApplicationUserService applicationUserService;
    @Mock
    private MailService mailService;

    private ScheduledTask task;

    @BeforeEach
    void init() {
        task = new ScheduledTask(overclockersFetchingService, applicationUserService, mailService);
    }

    @Test
    void processTopics() {
        List<ApplicationUser> applicationUsers = Arrays.asList(ApplicationUser.builder().build(), ApplicationUser.builder().build());
        when(applicationUserService.findAllEnabledUsers()).thenReturn(applicationUsers);

        task.processTopics();

        verify(overclockersFetchingService, times(1)).fetchAndSaveTopics();
        verify(applicationUserService, times(1)).findAllEnabledUsers();
        verify(mailService, times(applicationUsers.size())).processUserRequestEmail(any(ApplicationUser.class));
    }
}