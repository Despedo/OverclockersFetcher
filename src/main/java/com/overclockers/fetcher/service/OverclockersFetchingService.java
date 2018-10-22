package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.Topic;
import com.overclockers.fetcher.entity.User;
import com.overclockers.fetcher.parser.OverclockersElementParser;
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
    TopicService topicService;
    @Autowired
    UserService userService;

    private boolean isColdStart = true;

    @Override
    public void saveTopics() {
        String firstPageUrl = HOST_URL + FIRST_PAGE_SELLING_PATH;

        if (isColdStart) {
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
        } else {
            savePage(firstPageUrl);
            isColdStart = false;
        }
    }

    private void savePage(String url) {
        Document document = getDocumentFromUrl(url);
        Elements elements = elementParser.getPageTopicElements(document);

        for (Element element : elements) {
            User user = getUser(element);
            User savedUser = userService.saveUser(user);

            Topic topic = getTopic(element);
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

    private Topic getTopic(Element element) {
        String location = elementParser.getTopicLocation(element);
        String title = elementParser.getTopicTitle(element);
        String forumId = elementParser.getTopicForumId(element);
        String link = elementParser.getTopicLink(element);
        LocalDateTime lastMessageDateTime = elementParser.getLastMessageDateTime(element);

        Document document = getDocumentFromUrl(link);
        LocalDateTime createdDateTime = getFirstMessageDateTime(document);
        LocalDateTime updatedDateTime = getUpdatedDateTime(document);

        return Topic.builder()
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

    private User getUser(Element element) {
        String username = elementParser.getAuthorUsername(element);
        String forumId = elementParser.getAuthorProfileForumId(element);
        LocalDateTime registeredDateTime = LocalDateTime.now();

        return User.builder()
                .username(username)
                .userForumId(Long.valueOf(forumId))
                .registeredDateTime(registeredDateTime)
                .build();
    }

}
