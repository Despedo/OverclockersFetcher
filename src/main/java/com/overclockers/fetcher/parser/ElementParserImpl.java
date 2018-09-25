package com.overclockers.fetcher.parser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import static com.overclockers.fetcher.ForumConstants.MAIN_URL;
import static com.overclockers.fetcher.ForumConstants.TOPIC_URL;
import static com.overclockers.fetcher.ForumConstants.USER_PROFILE_URL;

@Component
public class ElementParserImpl implements ElementParser {

    private String getCityFromTopic(String topic) {
        int start = topic.indexOf('[') + 1;
        int end = topic.indexOf(']');
        return topic.substring(start, end);
    }

    private String removeCityFromTopic(String topic) {
        return topic.substring(topic.indexOf(']') + 2);
    }

    private String getUserProfileId(String href) {
        int start = href.indexOf(USER_PROFILE_URL) + USER_PROFILE_URL.length();
        int end = href.indexOf("&sid=");
        return href.substring(start, end);
    }

    private String getUserProfileLink(String href) {
        return MAIN_URL + USER_PROFILE_URL + getUserProfileId(href);
    }

    private String getTopicId(String href) {
        int start = href.indexOf(TOPIC_URL) + TOPIC_URL.length();
        int end = href.indexOf("&sid=");
        return href.substring(start, end);
    }

    private String getTopicLink(String href) {
        return MAIN_URL + TOPIC_URL + getTopicId(href);
    }

    @Override
    public String getTopicTitle(Element element) {
        Elements topicElements = element.getElementsByAttributeValue("class", "topictitle");
        return removeCityFromTopic(topicElements.text());
    }

    @Override
    public String getTopicCity(Element element) {
        Elements topicElements = element.getElementsByAttributeValue("class", "topictitle");
        return getCityFromTopic(topicElements.text());
    }

    @Override
    public String getTopicId(Element element) {
        Elements topicElements = element.getElementsByAttributeValue("class", "topictitle");
        String topicHref = topicElements.select("a").attr("href");
        return getTopicId(topicHref);
    }

    @Override
    public String getTopicLink(Element element) {
        Elements topicElements = element.getElementsByAttributeValue("class", "topictitle");
        String topicHref = topicElements.select("a").attr("href");
        return getTopicLink(topicHref);
    }

    @Override
    public String getAuthorUsername(Element element) {
        Elements authorElements = element.getElementsByAttributeValue("class", "author");
        return authorElements.select("a").text();
    }

    @Override
    public String getAuthorProfileId(Element element) {
        Elements authorElements = element.getElementsByAttributeValue("class", "author");
        String profileHref = authorElements.select("a").attr("href");
        return getUserProfileId(profileHref);
    }

    @Override
    public String getAuthorProfileLink(Element element) {
        Elements authorElements = element.getElementsByAttributeValue("class", "author");
        String profileHref = authorElements.select("a").attr("href");
        return getUserProfileLink(profileHref);
    }
}
