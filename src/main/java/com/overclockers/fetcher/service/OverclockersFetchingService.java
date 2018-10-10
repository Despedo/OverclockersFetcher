package com.overclockers.fetcher.service;

import com.overclockers.fetcher.HibernateUtil;
import com.overclockers.fetcher.entity.Topic;
import com.overclockers.fetcher.entity.User;
import com.overclockers.fetcher.parser.OverclockersElementParser;
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

import static com.overclockers.fetcher.constants.OverclockersConstants.*;

@Slf4j
@Service
public class OverclockersFetchingService implements FetchingService {

    private static final String DB_MULTIPLE_TOPICS_ERROR = "DB contains more that one topic with the same title";
    private static final String DB_MULTIPLE_USERS_ERROR = "DB contains more that one user with the same username";
    private static final String URL_CONNECTING_ERROR = "Error connecting to the URL";

    @Value("${pages.fetching.size:1}")
    private int pagesFetchingSize;

    @Autowired
    OverclockersElementParser elementParser;

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

        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();

        for (Element element : elements) {
            saveUsersAndTopics(session, element);
        }

        transaction.commit();
        HibernateUtil.closeSession();
    }

    private Elements getElementsFromUrl(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(URL_CONNECTING_ERROR + " {}:", url);
            throw new IllegalArgumentException(URL_CONNECTING_ERROR);
        }
        return doc.getElementsByAttributeValueMatching(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_VALUE);
    }

    private void saveUsersAndTopics(Session session, Element element) {
        User user = getUserFromElement(element);
        user = checkAndSaveUser(session, user);

        Topic topic = getTopicFromElement(element, user);
        checkAndSaveTopic(session, topic);
    }

    private Topic checkAndSaveTopic(Session session, Topic topic) {
        Topic existingTopic = getTopicByTopicLink(session, topic.getTopicLink());
        if (existingTopic != null) {
            topic = existingTopic;
        } else {
            session.save(topic);
        }
        return topic;
    }

    private User checkAndSaveUser(Session session, User user) {
        User existingUser = getUserByProfileLink(session, user.getProfileLink());
        if (existingUser != null) {
            user = existingUser;
        } else {
            session.save(user);
        }
        return user;
    }

    private Topic getTopicFromElement(Element element, User user) {
        String topicCity = elementParser.getTopicCity(element);
        String topicTitle = elementParser.getTopicTitle(element);
        String topicLink = elementParser.getTopicLink(element);
        return Topic.builder()
                .topicStarter(user)
                .city(topicCity)
                .title(topicTitle)
                .topicLink(topicLink)
                .build();
    }

    private User getUserFromElement(Element element) {
        String authorUsername = elementParser.getAuthorUsername(element);
        String authorProfileLink = elementParser.getAuthorProfileLink(element);

        return User.builder()
                .username(authorUsername)
                .profileLink(authorProfileLink)
                .build();
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
