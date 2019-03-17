package com.overclockers.fetcher.mail;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
@Import(SimpleJavaMailSpringSupport.class)
@AllArgsConstructor
public class MailServiceImpl implements MailService {

    private static final String SEARCH_REQUEST_EMAIL_SUBJECT = "Topics according to your request";
    private static final String REGISTRATION_EMAIL_SUBJECT = "Registration Confirmation";
    private static final String SENDER_NAME = "Overclockers FS";

    private HtmlRender render;
    private ForumTopicService topicService;
    private Mailer mailer;
    private SearchRequestService requestService;

    @Async
    @Override
    public void processUserRequestEmail(ApplicationUser user) {
        List<SearchRequest> searchRequests = requestService.findSearchRequestsByUserId(user.getUserId());

        Set<ForumTopic> topics = new HashSet<>();
        for (SearchRequest searchRequest : searchRequests) {
            topics.addAll(topicService.findTopicsByTitle(searchRequest.getRequest()));
        }

        if (!topics.isEmpty()) {
            String htmlText = render.renderHtmlTextForSearchRequestEmail(searchRequests, topics);

            Email email = EmailBuilder.startingBlank()
                    .from(SENDER_NAME, mailer.getServerConfig().getUsername())
                    .to(user.getEmail())
                    .withSubject(SEARCH_REQUEST_EMAIL_SUBJECT)
                    .withHTMLText(htmlText)
                    .buildEmail();

            log.info("Found {} topics by requests for user '{}', sending email.", topics.size(), user.getEmail());
            log.info("Topics [{}]", topics);
            mailer.sendMail(email);

            // Todo chance logic to set sent status only for current user
            topicService.updateTopicsStatuses(topics, true);
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

        mailer.sendMail(email);
    }
}
