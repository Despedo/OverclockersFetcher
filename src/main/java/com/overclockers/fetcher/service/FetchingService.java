package com.overclockers.fetcher.service;

import com.overclockers.fetcher.HibernateUtil;
import com.overclockers.fetcher.model.Topic;
import com.overclockers.fetcher.model.User;
import com.overclockers.fetcher.parser.ElementParserImpl;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.Query;
import java.io.IOException;
import java.util.List;

import static com.overclockers.fetcher.constants.ForumConstants.ELEMENT_CLASS_KEY;
import static com.overclockers.fetcher.constants.ForumConstants.ELEMENT_TOPIC_VALUE;

@Slf4j
@Service
public class FetchingService {

    private static final String DB_MULTIPLE_TOPICS_ERROR = "DB contains more that one topic with the same title";
    private static final String DB_MULTIPLE_USERS_ERROR = "DB contains more that one user with the same username";
    private static final String URL_CONNECTING_ERROR = "Error connecting to the URL";

    @Value("${first.page.url}")
    private String firstPageUrl;
    @Value("${pages.fetching.size:10}")
    private int pagesFetchingSize;

    @Autowired
    ElementParserImpl elementParser;

    public void saveTopics() {
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
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(URL_CONNECTING_ERROR + " {}:", url);
            throw new IllegalArgumentException(URL_CONNECTING_ERROR);
        }
        Elements elements = doc.getElementsByAttributeValueMatching(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_VALUE);

        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();

        for (Element element : elements) {
            saveUsersAndTopics(session, element);
        }

        transaction.commit();
        HibernateUtil.closeSession();
    }

    private void saveUsersAndTopics(Session session, Element element) {
        String authorUsername = elementParser.getAuthorUsername(element);
        String authorProfileLink = elementParser.getAuthorProfileLink(element);
        String topicCity = elementParser.getTopicCity(element);
        String topicTitle = elementParser.getTopicTitle(element);
        String topicLink = elementParser.getTopicLink(element);

        User user = User.builder()
                .username(authorUsername)
                .profileLink(authorProfileLink)
                .build();

        User existingUser = getUserByProfileLink(session, user.getProfileLink());
        if (existingUser != null) {
            user = existingUser;
        } else {
            session.save(user);
        }

        Topic topic = Topic.builder()
                .topicStarter(user)
                .city(topicCity)
                .title(topicTitle)
                .topicLink(topicLink)
                .build();

        Topic existingTopic = getTopicByTopicLink(session, topic.getTopicLink());
        if (existingTopic == null) {
            session.save(topic);
        }
    }

    private User getUserByProfileLink(Session session, String profileLink) {
        Query query = session.createQuery("FROM User u WHERE u.profileLink = :profileLink");
        query.setParameter("profileLink", profileLink);
        List list = query.getResultList();
        if (list.size() > 1) {
            log.error(DB_MULTIPLE_USERS_ERROR);
            throw new IllegalStateException(DB_MULTIPLE_USERS_ERROR);
        }
        return list.isEmpty() ? null : (User) list.get(0);
    }

    private Topic getTopicByTopicLink(Session session, String topicLink) {
        Query query = session.createQuery("FROM Topic t WHERE t.topicLink = :topicLink");
        query.setParameter("topicLink", topicLink);
        List list = query.getResultList();
        if (list.size() > 1) {
            log.error(DB_MULTIPLE_TOPICS_ERROR);
            throw new IllegalStateException(DB_MULTIPLE_TOPICS_ERROR);
        }
        return list.isEmpty() ? null : (Topic) list.get(0);
    }

}
