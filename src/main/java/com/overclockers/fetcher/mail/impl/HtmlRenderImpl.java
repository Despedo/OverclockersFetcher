package com.overclockers.fetcher.mail.impl;

import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.mail.HtmlRender;
import j2html.tags.DomContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.overclockers.fetcher.constants.OverclockersConstants.HOST_URL;
import static com.overclockers.fetcher.constants.OverclockersConstants.TOPIC_PATH;
import static j2html.TagCreator.*;

@RequiredArgsConstructor
@Service
public class HtmlRenderImpl implements HtmlRender {

    private static final String IMG_URL = "https://imgur.com/RUa4aSe.png";

    @Override
    public String renderHtmlTextForSearchRequestEmail(Map<SearchRequest, List<ForumTopic>> topicsMap) {
        return html(
                body(
                        hr(),
                        img().withSrc(IMG_URL),
                        hr(),
                        generateTopicsContent(topicsMap)
                )
        ).renderFormatted();
    }

    @Override
    public String renderHtmlTextForRegistrationConfirmation(String href) {
        return html(
                body(
                        hr(),
                        img().withSrc(IMG_URL),
                        hr(),
                        text("To confirm your e-mail address, please click "),
                        a().withText("the link").withHref(href)
                )
        ).renderFormatted();
    }

    private DomContent generateTopicsContent(Map<SearchRequest, List<ForumTopic>> topicsMap) {
        return each(topicsMap.keySet(), searchRequest ->
                p(
                        strong("According to your request: " + searchRequest.getRequest()),
                        each(topicsMap.get(searchRequest), topic -> li(a(topic.getTitle()).withHref(getTopicUrl(topic))))
                ).with()
        );
    }

    private String getTopicUrl(ForumTopic topic) {
        return HOST_URL + TOPIC_PATH + topic.getTopicForumId();
    }
}
