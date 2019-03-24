package com.overclockers.fetcher.parser;

import org.jsoup.nodes.Element;

public interface ElementParser {
    String getTopicTitle(Element element);

    String getTopicLocation(Element element);

    String getTopicForumId(Element element);

    String getTopicLink(Element element);

    String getAuthorUsername(Element element);

    String getAuthorProfileForumId(Element element);
}
