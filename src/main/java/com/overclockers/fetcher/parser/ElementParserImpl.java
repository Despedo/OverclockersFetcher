package com.overclockers.fetcher.parser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.overclockers.fetcher.constants.ForumConstants.*;

@Component
public class ElementParserImpl implements ElementParser {

    private static final String USER_PROFILE_DELIMITER = "&u=";
    private static final String TOPIC_DELIMITER = "&t=";
    private static final String SID_DELIMITER = "&sid=";
    private static final String A_TAG = "a";
    private static final String HREF_ATTRIBUTE = "href";

    @Value("${user.profile.url}")
    private String userProfileUrl;
    @Value("${topic.url}")
    private String topicUrl;

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
        return userProfileUrl + getUserProfileId(href);
    }

    private String getTopicId(String href) {
        int start = href.indexOf(TOPIC_DELIMITER) + TOPIC_DELIMITER.length();
        int end = href.indexOf(SID_DELIMITER);
        return href.substring(start, end);
    }

    private String getTopicLink(String href) {
        return topicUrl + getTopicId(href);
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
