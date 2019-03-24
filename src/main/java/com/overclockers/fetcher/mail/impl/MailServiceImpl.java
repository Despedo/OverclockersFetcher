package com.overclockers.fetcher.mail.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.service.ForumTopicService;
import com.overclockers.fetcher.service.SearchRequestService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@Import(SimpleJavaMailSpringSupport.class)
@AllArgsConstructor
public class MailServiceImpl implements MailService {

    private static final String SEARCH_REQUEST_EMAIL_SUBJECT = "Topics according to your request";
    private static final String REGISTRATION_EMAIL_SUBJECT = "Registration Confirmation";
    private static final String SENDER_NAME = "Overclockers FS";

    private HtmlRenderImpl render;
    private ForumTopicService topicService;
    private Mailer mailer;
    private SearchRequestService requestService;

    @Async("threadPoolTaskExecutor")
    @Override
    public void processUserRequestEmail(ApplicationUser user) {
        List<SearchRequest> searchRequests = requestService.findSearchRequestsByUserId(user.getId());

        List<ForumTopic> topics = new ArrayList<>();
        for (SearchRequest searchRequest : searchRequests) {
            // ToDo implement finding by batch to avoid for
            topics.addAll(topicService.findTopicsForSending(searchRequest.getRequest(), user.getId()));
        }

        if (!topics.isEmpty()) {
            String htmlText = render.renderHtmlTextForSearchRequestEmail(searchRequests, topics);

            Email email = EmailBuilder.startingBlank()
                    .from(SENDER_NAME, mailer.getServerConfig().getUsername())
                    .to(user.getEmail())
                    .withSubject(SEARCH_REQUEST_EMAIL_SUBJECT)
                    .withHTMLText(htmlText)
                    .buildEmail();

            log.info("Found '{}' topics by requests '{}' for user '{}'.", topics.size(), searchRequests, user.getEmail());
            log.info("Sending email to '{}'", user.getEmail());
            mailer.sendMail(email);
            topicService.registerSentTopics(topics, user);
        } else {
            log.info("No topics were found by request for user '{}'", user.getEmail());
        }
    }

    @Override
    public void processRegistrationEmail(ApplicationUser user, String appUrl) {

        String htmlText = render.renderHtmlTextForRegistrationConfirmation(appUrl + "/confirm?token=" + user.getConfirmationToken());

        Email email = EmailBuilder.startingBlank()
                .from(SENDER_NAME, mailer.getServerConfig().getUsername())
                .to(user.getEmail())
                .withSubject(REGISTRATION_EMAIL_SUBJECT)
                .withHTMLText(htmlText)
                .buildEmail();

        log.info("Sending registration email to '{}'", user.getEmail());
        mailer.sendMail(email);
    }
}
