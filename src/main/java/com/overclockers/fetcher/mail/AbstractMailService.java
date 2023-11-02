package com.overclockers.fetcher.mail;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.service.ForumTopicService;
import com.overclockers.fetcher.service.SearchRequestService;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Log4j2
@SuperBuilder
public abstract class AbstractMailService implements MailService {

    protected static final String SEARCH_REQUEST_EMAIL_SUBJECT = "Topics according to your request";
    protected static final String REGISTRATION_EMAIL_SUBJECT = "Registration Confirmation";
    protected static final String SENDER_NAME = "Oversearch";

    @Value("${simplejavamail.smtp.username}")
    protected String senderAddress;

    protected final HtmlRender render;
    protected final ForumTopicService topicService;
    private final SearchRequestService requestService;

    protected Map<SearchRequest, List<ForumTopic>> getTopicsMap(ApplicationUser user) {
        List<SearchRequest> searchRequests = requestService.findSearchRequestsByUserId(user.getId());
        return topicService.findTopicsMapForSending(searchRequests, user.getId());
    }

    protected static void logUserRequests(ApplicationUser user, Map<SearchRequest, List<ForumTopic>> topicsMap) {
        long topicSize = topicsMap.values().stream().mapToLong(Collection::size).sum();
        List<SearchRequest> searchRequests = new ArrayList<>(topicsMap.keySet());
        log.info("Found '{}' topics by requests '{}' for user '{}'.", topicSize, searchRequests, user.getEmail());
    }

}
