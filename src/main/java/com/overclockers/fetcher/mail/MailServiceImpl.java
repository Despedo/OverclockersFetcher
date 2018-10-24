package com.overclockers.fetcher.mail;

import com.overclockers.fetcher.entity.Topic;
import com.overclockers.fetcher.service.TopicService;
import lombok.extern.log4j.Log4j2;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@Import(SimpleJavaMailSpringSupport.class)
public class MailServiceImpl implements MailService {

    private static final String EMAIL_SUBJECT = "Topics according to your request";
    private static final String SENDER_NAME = "Overclockers FS";

    @Autowired
    private HtmlRender render;
    @Autowired
    private TopicService topicService;
    @Autowired
    Mailer mailer;

    @Value("${search.request}")
    private String searchRequest;
    @Value("${app.user.email}")
    private String appUserEmail;

    @Override
    public void prepareAndSendEmail() {

        Set<String> stringSet = new HashSet<>(Arrays.asList(searchRequest.split(",")));

        Set<Topic> topics = new HashSet<>();
        for (String searchTitle : stringSet) {
            topics.addAll(topicService.findTopicsForSending(searchTitle));
        }

        if (!topics.isEmpty()) {
            String htmlText = render.renderHtmlTextForEmail(stringSet, topics);

            Email email = EmailBuilder.startingBlank()
                    .from(SENDER_NAME, mailer.getServerConfig().getUsername())
                    .to(appUserEmail)
                    .withSubject(EMAIL_SUBJECT)
                    .withHTMLText(htmlText)
                    .buildEmail();

            log.info("Found {} topics for the request '{}', sending email.", topics.size(), searchRequest);
            log.info("Topics [{}]", topics);
            mailer.sendMail(email);

            topicService.updateTopicsStatuses(topics, true);
        } else {
            log.info("No topics were found the request '{}'", searchRequest);
        }
    }
}
