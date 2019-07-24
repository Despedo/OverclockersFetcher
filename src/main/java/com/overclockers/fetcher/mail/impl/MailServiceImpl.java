package com.overclockers.fetcher.mail.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.mail.HtmlRender;
import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.service.ForumTopicService;
import com.overclockers.fetcher.service.SearchRequestService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final String SEARCH_REQUEST_EMAIL_SUBJECT = "Topics according to your request";
    private static final String REGISTRATION_EMAIL_SUBJECT = "Registration Confirmation";
    private static final String SENDER_NAME = "Overclockers FS";

    @Value("${simplejavamail.smtp.username}")
    private String senderAddress;

    @NonNull
    private HtmlRender render;
    @NonNull
    private ForumTopicService topicService;
    @NonNull
    private Mailer mailer;
    @NonNull
    private SearchRequestService requestService;

    @Async("threadPoolTaskExecutor")
    @Override
    public synchronized void processUserRequestEmail(ApplicationUser user) {
        List<SearchRequest> searchRequests = requestService.findSearchRequestsByUserId(user.getId());
        Map<SearchRequest, List<ForumTopic>> topicsMap = topicService.findTopicsMapForSending(searchRequests, user.getId());

        if (!topicsMap.isEmpty()) {
            String htmlText = render.renderHtmlTextForSearchRequestEmail(topicsMap);

            Email email = EmailBuilder.startingBlank()
                    .from(SENDER_NAME, senderAddress)
                    .to(user.getEmail())
                    .withSubject(SEARCH_REQUEST_EMAIL_SUBJECT)
                    .withHTMLText(htmlText)
                    .buildEmail();

            log.info("Found '{}' topics by requests '{}' for user '{}'.", topicsMap.values().stream()
                    .flatMap(Collection::stream).collect(Collectors.toList()).size(), searchRequests, user.getEmail());
            log.info("Sending email to '{}'.", user.getEmail());
            mailer.sendMail(email);
            topicService.registerSentTopics(topicsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList()), user);
        } else {
            log.info("No topics were found by request for user '{}'", user.getEmail());
        }
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

        log.info("Sending registration email to '{}'", user.getEmail());
        mailer.sendMail(email);
    }
}
