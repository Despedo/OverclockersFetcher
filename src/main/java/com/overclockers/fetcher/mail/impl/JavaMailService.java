package com.overclockers.fetcher.mail.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.mail.AbstractMailService;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@ConditionalOnProperty(name = "mail.service", havingValue = "javaMailService", matchIfMissing = true)
@SuperBuilder
public class JavaMailService extends AbstractMailService {

    private final JavaMailSender emailSender;

    @SneakyThrows
    @Async("threadPoolTaskExecutor")
    @Override
    public synchronized void processUserRequestEmail(ApplicationUser user) {
        Map<SearchRequest, List<ForumTopic>> topicsMap = getTopicsMap(user);

        if (!topicsMap.isEmpty()) {
            String htmlText = render.renderHtmlTextForSearchRequestEmail(topicsMap);

            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(senderAddress, SENDER_NAME);
            helper.setTo(user.getEmail());
            helper.setSubject(SEARCH_REQUEST_EMAIL_SUBJECT);
            helper.setText(htmlText, true);

            logUserRequests(user, topicsMap);
            emailSender.send(mimeMessage);
            log.info("Email sent to '{}'.", user.getEmail());
            topicService.registerSentTopics(topicsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList()), user);
        } else {
            log.info("No topics were found by request for user '{}'", user.getEmail());
        }
    }

    @SneakyThrows
    @Override
    public void sendTestEmail() {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setFrom(senderAddress, SENDER_NAME);
        helper.setTo("despedo@gmail.com");
        helper.setSubject("Hello from java mail");
        helper.setText("<h3>Hello from Ukraine!!!</h3>", true);
        emailSender.send(mimeMessage);
        log.info("Test email sent with java mail");
    }

    @SneakyThrows
    @Async("threadPoolTaskExecutor")
    @Override
    public void processRegistrationEmail(ApplicationUser user, String appUrl) {

        String htmlText = render.renderHtmlTextForRegistrationConfirmation(appUrl + "/confirm?token=" + user.getConfirmationToken());

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setFrom(senderAddress, SENDER_NAME);
        helper.setTo(user.getEmail());
        helper.setSubject(REGISTRATION_EMAIL_SUBJECT);
        helper.setText(htmlText, true);

        emailSender.send(mimeMessage);
        log.info("Registration email sent to '{}'", user.getEmail());
    }
}
