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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        List<String> searchList = Arrays.asList(searchRequest.split(","));

        List<Topic> topics = new ArrayList<>();
        for (String searchTitle : searchList) {
            topics.addAll(topicService.findTopicsByLikeTitle(searchTitle));
        }

        if (!topics.isEmpty()) {
            log.info("Found {} topics for the request '{}'", topics.size(), searchRequest);
            String htmlText = render.renderHtmlTextForEmail(searchList, topics);

            Email email = EmailBuilder.startingBlank()
                    .from(SENDER_NAME, mailer.getServerConfig().getUsername())
                    .to(appUserEmail)
                    .withSubject(EMAIL_SUBJECT)
                    .withHTMLText(htmlText)
                    .buildEmail();

            mailer.sendMail(email);

            topicService.updateTopicsStatus(topics, true);
        } else {
            log.info("No topics were found the request '{}'", searchRequest);
        }
    }
}
