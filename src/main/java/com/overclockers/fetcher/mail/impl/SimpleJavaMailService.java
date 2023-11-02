package com.overclockers.fetcher.mail.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.mail.AbstractMailService;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@Import(SimpleJavaMailSpringSupport.class)
@ConditionalOnProperty(name = "mail.service", havingValue = "simpleJavaMailService")
@SuperBuilder
public class SimpleJavaMailService extends AbstractMailService {

    private final Mailer mailer;

    @Async("threadPoolTaskExecutor")
    @Override
    public synchronized void processUserRequestEmail(ApplicationUser user) {
        Map<SearchRequest, List<ForumTopic>> topicsMap = getTopicsMap(user);

        if (!topicsMap.isEmpty()) {
            String htmlText = render.renderHtmlTextForSearchRequestEmail(topicsMap);

            Email email = EmailBuilder.startingBlank()
                    .from(SENDER_NAME, senderAddress)
                    .to(user.getEmail())
                    .withSubject(SEARCH_REQUEST_EMAIL_SUBJECT)
                    .withHTMLText(htmlText)
                    .buildEmail();

            logUserRequests(user, topicsMap);
            mailer.sendMail(email);
            log.info("Email sent to '{}'.", user.getEmail());
            topicService.registerSentTopics(topicsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList()), user);
        } else {
            log.info("No topics were found by request for user '{}'", user.getEmail());
        }
    }

    @Override
    public void sendTestEmail() {
        Email email = EmailBuilder.startingBlank()
                .from(SENDER_NAME, senderAddress)
                .to("despedo@gmail.com")
                .withSubject("Hello from simple java mail")
                .withHTMLText("<h3>Hello from Ukraine!!!</h3>")
                .buildEmail();
        mailer.sendMail(email);
        log.info("Test email sent with simple java mail");
    }

    @Async("threadPoolTaskExecutor")
    @Override
    public void processRegistrationEmail(ApplicationUser user, String appUrl) {

        String htmlText = render.renderHtmlTextForRegistrationConfirmation(appUrl + "/confirm?token=" + user.getConfirmationToken());

        Email email = EmailBuilder.startingBlank()
                .from(SENDER_NAME, senderAddress)
                .to(user.getEmail())
                .withSubject(REGISTRATION_EMAIL_SUBJECT)
                .withHTMLText(htmlText)
                .buildEmail();

        mailer.sendMail(email);
        log.info("Registration email sent to '{}'", user.getEmail());
    }
}
