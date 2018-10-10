package com.overclockers.fetcher.parser;

import org.jsoup.nodes.Element;

import java.time.LocalDateTime;

public interface ElementParser {
    String getTopicTitle(Element element);

    String getTopicLocation(Element element);

    String getTopicForumId(Element element);

    String getTopicLink(Element element);

    LocalDateTime getLastMessageDateTime(Element element);

    String getAuthorUsername(Element element);

    String getAuthorProfileForumId(Element element);

    String getAuthorProfileLink(Element element);
}
