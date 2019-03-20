package com.overclockers.fetcher.mail;

import com.overclockers.fetcher.entity.ApplicationUser;

public interface MailService {
    void processUserRequestEmail(ApplicationUser user);

    void processRegistrationEmail(ApplicationUser user, String appUrl);
}
