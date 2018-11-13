package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.ForumUser;
import com.overclockers.fetcher.parser.OverclockersElementParser;
import com.overclockers.fetcher.service.FetchingService;
import com.overclockers.fetcher.service.ForumTopicService;
import com.overclockers.fetcher.service.ForumUserService;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.overclockers.fetcher.constants.OverclockersConstants.*;

@Log4j2
@Service
public class OverclockersFetchingService implements FetchingService {

    private static final String URL_CONNECTING_ERROR = "Error connecting to the URL";

    @Autowired
    OverclockersElementParser elementParser;
    @Autowired
    ForumTopicService topicService;
    @Autowired
    ForumUserService userService;

    private boolean isColdStart = true;

    @Override
    public void saveTopics() {
        String firstPageUrl = HOST_URL + FIRST_PAGE_SELLING_PATH;

        if (isColdStart) {
            log.info("Fetching cold start");
            int coldStartFetchingSize = 10;
            int nextPage = 0;
            for (int i = 0; i < coldStartFetchingSize; i++) {
                if (i == 0) {
                    savePage(firstPageUrl);
                } else {
                    String nextPageUrl = firstPageUrl + "&start=" + nextPage;
                    int nextPageShift = 40;
                    nextPage += nextPageShift;
                    savePage(nextPageUrl);
                }
            }
            isColdStart = false;
        } else {
            savePage(firstPageUrl);
        }
    }

    private void savePage(String url) {
        Document document = getDocumentFromUrl(url);
        Elements elements = elementParser.getPageTopicElements(document);

        for (Element element : elements) {
            ForumUser user = getUser(element);
            ForumUser savedUser = userService.saveUser(user);

            ForumTopic topic = getTopic(element);
            topic.setUser(savedUser);
            topicService.saveOrUpdateTopic(topic);
        }

    }

    public Document getDocumentFromUrl(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(URL_CONNECTING_ERROR + ": {}", url);
            throw new IllegalArgumentException(URL_CONNECTING_ERROR);
        }
        return doc;
    }

    private ForumTopic getTopic(Element element) {
        String location = elementParser.getTopicLocation(element);
        String title = elementParser.getTopicTitle(element);
        String forumId = elementParser.getTopicForumId(element);
        String link = elementParser.getTopicLink(element);
        LocalDateTime lastMessageDateTime = elementParser.getLastMessageDateTime(element);

        Document document = getDocumentFromUrl(link);
        LocalDateTime createdDateTime = getFirstMessageDateTime(document);
        LocalDateTime updatedDateTime = getUpdatedDateTime(document);

        return ForumTopic.builder()
                .location(location)
                .title(title)
                .topicForumId(Long.valueOf(forumId))
                .createdDateTime(createdDateTime)
                .updatedDateTime(updatedDateTime)
                .lastMessageDateTime(lastMessageDateTime)
                .build();
    }

    private LocalDateTime getUpdatedDateTime(Document document) {
        LocalDateTime updatedDateTime;
        Elements noticeElements = document.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_NOTICE_VALUE);
        if (!noticeElements.isEmpty()) {
            Element noticeEditedElement = noticeElements.first();
            updatedDateTime = elementParser.getDateTime(noticeEditedElement);
        } else {
            updatedDateTime = null;
        }
        return updatedDateTime;
    }

    private LocalDateTime getFirstMessageDateTime(Document document) {
        Elements authorElements = document.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_AUTHOR_VALUE);
        Element firstMessageElement = authorElements.select("p").first();
        return elementParser.getDateTime(firstMessageElement);
    }

    private ForumUser getUser(Element element) {
        String username = elementParser.getAuthorUsername(element);
        String forumId = elementParser.getAuthorProfileForumId(element);
        LocalDateTime registeredDateTime = LocalDateTime.now();

        return ForumUser.builder()
                .username(username)
                .userForumId(Long.valueOf(forumId))
                .registeredDateTime(registeredDateTime)
                .build();
    }

}
