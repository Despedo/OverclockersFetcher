package com.overclockers.fetcher.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.overclockers.fetcher.constants.OverclockersConstants.*;

@Slf4j
@Component
public class OverclockersElementParser implements ElementParser {

    private static final String DATE_TIME_FORMAT_ERROR = "Last message date time format is not valid";
    private static final String USER_PROFILE_DELIMITER = "&u=";
    private static final String TOPIC_DELIMITER = "&t=";
    private static final String SID_DELIMITER = "&sid=";
    private static final String A_TAG = "a";
    private static final String HREF_ATTRIBUTE = "href";

    private String getCityFromTopic(String topic) {
        int start = topic.indexOf('[') + 1;
        int end = topic.indexOf(']');
        return topic.substring(start, end);
    }

    private String removeCityFromTopic(String topic) {
        return topic.substring(topic.indexOf(']') + 2);
    }

    private String getUserProfileId(String href) {
        int start = href.indexOf(USER_PROFILE_DELIMITER) + USER_PROFILE_DELIMITER.length();
        int end = href.indexOf(SID_DELIMITER);
        return href.substring(start, end);
    }

    private String getUserProfileLink(String href) {
        return HOST_URL + USER_PROFILE_PATH + getUserProfileId(href);
    }

    private String getTopicId(String href) {
        int start = href.indexOf(TOPIC_DELIMITER) + TOPIC_DELIMITER.length();
        int end = href.indexOf(SID_DELIMITER);
        return href.substring(start, end);
    }

    private String getTopicLink(String href) {
        return HOST_URL + TOPIC_PATH + getTopicId(href);
    }

    @Override
    public String getTopicTitle(Element element) {
        Elements topicElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_TITLE_VALUE);
        return removeCityFromTopic(topicElements.text());
    }

    @Override
    public String getTopicCity(Element element) {
        Elements topicElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_TITLE_VALUE);
        return getCityFromTopic(topicElements.text());
    }

    @Override
    public String getTopicId(Element element) {
        Elements topicElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_TITLE_VALUE);
        String topicHref = topicElements.select(A_TAG).attr(HREF_ATTRIBUTE);
        return getTopicId(topicHref);
    }

    @Override
    public String getTopicLink(Element element) {
        Elements topicElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_TOPIC_TITLE_VALUE);
        String topicHref = topicElements.select(A_TAG).attr(HREF_ATTRIBUTE);
        return getTopicLink(topicHref);
    }

    @Override
    public LocalDateTime getLastMessageDateTime(Element element) {
        Element lastMessageElement = element.getElementsByAttributeValue(ELEMENT_TOPIC_KEY, ELEMENT_LAST_MESSAGE_VALUE).first();
        String lastMessageDateTime = lastMessageElement.text();
        boolean isDateTimeValid = lastMessageDateTime.matches("^\\d{2}[.]\\d{2}[.]\\d{4}\\s\\d{2}[:]\\d{2}$");
        if (!isDateTimeValid) {
            log.error(DATE_TIME_FORMAT_ERROR + ": {}", lastMessageDateTime);
            throw new IllegalArgumentException(DATE_TIME_FORMAT_ERROR);
        }

        return parseLocalDateTime(lastMessageDateTime);
    }

    private LocalDateTime parseLocalDateTime(String lastMessageDateTime) {
        int dayOfMonth = Integer.parseInt(lastMessageDateTime.substring(0, 2));
        int month = Integer.parseInt(lastMessageDateTime.substring(3, 5));
        int year = Integer.parseInt(lastMessageDateTime.substring(6, 10));
        int hour = Integer.parseInt(lastMessageDateTime.substring(11, 13));
        int minute = Integer.parseInt(lastMessageDateTime.substring(14, 16));

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute);
    }

    @Override
    public String getAuthorUsername(Element element) {
        Elements authorElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_AUTHOR_VALUE);
        return authorElements.select(A_TAG).text();
    }

    @Override
    public String getAuthorProfileId(Element element) {
        Elements authorElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_AUTHOR_VALUE);
        String profileHref = authorElements.select(A_TAG).attr(HREF_ATTRIBUTE);
        return getUserProfileId(profileHref);
    }

    @Override
    public String getAuthorProfileLink(Element element) {
        Elements authorElements = element.getElementsByAttributeValue(ELEMENT_CLASS_KEY, ELEMENT_AUTHOR_VALUE);
        String profileHref = authorElements.select(A_TAG).attr(HREF_ATTRIBUTE);
        return getUserProfileLink(profileHref);
    }
}
