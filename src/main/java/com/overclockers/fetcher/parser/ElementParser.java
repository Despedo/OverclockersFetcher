package com.overclockers.fetcher.parser;

import org.jsoup.nodes.Element;

public interface ElementParser {
    String getTopicTitle(Element element);

    String getTopicCity(Element element);

    String getTopicId(Element element);

    String getTopicLink(Element element);

    String getAuthorUsername(Element element);

    String getAuthorProfileId(Element element);

    String getAuthorProfileLink(Element element);
}
