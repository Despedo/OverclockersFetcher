package com.overclockers.fetcher.mail;

import com.overclockers.fetcher.entity.ApplicationUser;

public interface MailService {
    void prepareAndSendSearchResults();

    void prepareAndSendRegistrationEmail(ApplicationUser user, String appUrl);
}
