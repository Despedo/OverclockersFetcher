package com.overclockers.fetcher.parser;

import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.ForumUser;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.overclockers.fetcher.constants.OverclockersConstants.*;

@Log4j2
@Component
public class OverclockersParser {

    private static final String DATE_TIME_REGEX = "\\d{2}.\\d{2}.\\d{4}\\s\\d{2}:\\d{2}";
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";
    private static final String DATE_TIME_FORMAT_ERROR = "Message date time format is not valid";
    private static final String USER_PROFILE_DELIMITER = "&u=";
    private static final String TOPIC_DELIMITER = "&t=";
    private static final String SID_DELIMITER = "&sid=";
    private static final String A_TAG = "a";
    private static final String HREF_ATTRIBUTE = "href";

    public List<ForumTopic> getForumTopics(String url) {
        Elements elements = getPageTopicElements(getDocumentFromUrl(url));

        return elements.stream().map
                (element -> {
                    ForumTopic topic = getTopic(element);
                    ForumUser user = getUser(element);
                    topic.setUser(user);
                    return topic;
                }).collect(Collectors.toList());
    }

    private String removeCityFromTopic(String topic) {
        return topic.substring(topic.indexOf(']') + 2);
    }

    private String getUserProfileForumId(String href) {
        int start = href.indexOf(USER_PROFILE_DELIMITER) + USER_PROFILE_DELIMITER.length();
        int end = href.indexOf(SID_DELIMITER);
        return href.substring(start, end);
    }

    private String getTopicForumId(String href) {
        int start = href.indexOf(TOPIC_DELIMITER) + TOPIC_DELIMITER.length();
        int end = href.indexOf(SID_DELIMITER);
        return href.substring(start, end);
    }

    private String getTopicLink(String href) {
        return HOST_URL + TOPIC_PATH + getTopicForumId(href);
    }

    private String getTopicTitle(Element element) {
        Elements topicElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_TITLE_VALUE);
        return removeCityFromTopic(topicElements.text());
    }

    private String getTopicLocation(Element element) {
        Elements topicElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_TITLE_VALUE);
        String topicText = topicElements.text();
        if (topicText.contains("[") && topicText.contains("]")) {
            int start = topicText.indexOf('[') + 1;
            int end = topicText.indexOf(']');
            return topicText.substring(start, end);
        } else {
            return null;
        }
    }

    private String getTopicForumId(Element element) {
        Elements topicElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_TITLE_VALUE);
        String topicHref = topicElements.select(A_TAG).attr(HREF_ATTRIBUTE);
        return getTopicForumId(topicHref);
    }

    private String getTopicLink(Element element) {
        Elements topicElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_TITLE_VALUE);
        String topicHref = topicElements.select(A_TAG).attr(HREF_ATTRIBUTE);
        return getTopicLink(topicHref);
    }

    private Elements getPageTopicElements(Document document) {
        return document.getElementsByAttributeValueMatching(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_VALUE_REGEXP);
    }

    private static final String URL_CONNECTING_ERROR = "Error connecting to the URL";

    private Document getDocumentFromUrl(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(URL_CONNECTING_ERROR + ": {}", url);
            throw new IllegalArgumentException(URL_CONNECTING_ERROR);
        }
        return doc;
    }

    private ForumUser getUser(Element element) {
        return ForumUser.builder()
                .nickname(getAuthorUsername(element))
                .userForumId(Long.valueOf(getAuthorProfileForumId(element)))
                .build();
    }

    private ForumTopic getTopic(Element element) {
        Document topicDocument = getDocumentFromUrl(getTopicLink(element));

        return ForumTopic.builder()
                .location(getTopicLocation(element))
                .title(getTopicTitle(element))
                .topicForumId(Long.valueOf(getTopicForumId(element)))
                .topicCreatedDateTime(getFirstMessageDateTime(topicDocument))
                .topicUpdatedDateTime(getUpdatedDateTime(topicDocument))
                .build();
    }

    private ZonedDateTime getUpdatedDateTime(Document document) {
        ZonedDateTime updatedDateTime;
        Elements noticeElements = document.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_NOTICE_VALUE);
        if (!noticeElements.isEmpty()) {
            Element noticeEditedElement = noticeElements.first();
            updatedDateTime = getDateTime(noticeEditedElement);
        } else {
            updatedDateTime = null;
        }
        return updatedDateTime;
    }

    private ZonedDateTime getFirstMessageDateTime(Document document) {
        Elements authorElements = document.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_AUTHOR_VALUE);
        Element firstMessageElement = authorElements.select("p").first();
        return getDateTime(firstMessageElement);
    }

    private String getAuthorUsername(Element element) {
        Elements authorElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_AUTHOR_VALUE);
        return authorElements.select(A_TAG).text();
    }

    private String getAuthorProfileForumId(Element element) {
        Elements authorElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_AUTHOR_VALUE);
        String profileHref = authorElements.select(A_TAG).attr(HREF_ATTRIBUTE);
        return getUserProfileForumId(profileHref);
    }

    private ZonedDateTime getDateTime(Element element) {
        Pattern dateTimePattern = Pattern.compile("(" + DATE_TIME_REGEX + ")");
        Matcher dateTimeMatcher = dateTimePattern.matcher(element.text());

        String messageDateTime = "";
        if (dateTimeMatcher.find()) {
            messageDateTime = dateTimeMatcher.group(1);
        }

        if (!messageDateTime.matches(DATE_TIME_REGEX)) {
            log.error(DATE_TIME_FORMAT_ERROR + ": {}", messageDateTime);
            throw new IllegalArgumentException(DATE_TIME_FORMAT_ERROR);
        }

        return convertToZonedUtc(LocalDateTime.parse(messageDateTime, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
    }

    private ZonedDateTime convertToZonedUtc(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Europe/Helsinki"));
        return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
    }
}
