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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
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

    @Value("${pages.fetching.size:1}")
    private int pagesFetchingSize;

    @Autowired
    private ApplicationArguments arguments;

    @Override
    public void saveTopics() {
        String firstPageUrl = HOST_URL + FIRST_PAGE_SELLING_PATH;
        int nextPage = 0;
        for (int i = 0; i < pagesFetchingSize; i++) {
            if (i == 0) {
                savePage(firstPageUrl);
            } else {
                int nextPageShift = 40;
                nextPage += nextPageShift;
                String nextPageUrl = firstPageUrl + "&start=" + nextPage;
                savePage(nextPageUrl);
            }
        }
    }

    private void savePage(String url) {
        Elements elements = getElementsFromUrl(url);

        for (Element element : elements) {
            User user = getUserFromElement(element);
            User savedUser = userService.saveUser(user);

            Topic topic = getTopicFromElement(element);
            topic.setUser(savedUser);
            topicService.saveTopic(topic);
        }

    }

    private Elements getElementsFromUrl(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(URL_CONNECTING_ERROR + ": {}", url);
            throw new IllegalArgumentException(URL_CONNECTING_ERROR);
        }
        return doc.getElementsByAttributeValueMatching(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_VALUE);
    }

    private Topic getTopicFromElement(Element element) {
        String topicLocation = elementParser.getTopicLocation(element);
        String topicTitle = elementParser.getTopicTitle(element);
        String topicForumId = elementParser.getTopicForumId(element);
        LocalDateTime createdDateTime = LocalDateTime.now();
        return Topic.builder()
                .location(topicLocation)
                .title(topicTitle)
                .topicForumId(Long.valueOf(topicForumId))
                .createdDateTime(createdDateTime)
                .build();
    }

    private User getUserFromElement(Element element) {
        String authorUsername = elementParser.getAuthorUsername(element);
        String userForumId = elementParser.getAuthorProfileForumId(element);
        LocalDateTime createdDateTime = LocalDateTime.now();

        return User.builder()
                .username(authorUsername)
                .userForumId(Long.valueOf(userForumId))
                .createdDateTime(createdDateTime)
                .build();
    }

}
