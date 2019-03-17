package com.overclockers.fetcher.mail;

import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;
import j2html.tags.DomContent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.overclockers.fetcher.constants.OverclockersConstants.HOST_URL;
import static com.overclockers.fetcher.constants.OverclockersConstants.TOPIC_PATH;
import static j2html.TagCreator.*;

@Service
public class HtmlRender {

    private static final String IMG_URL = "https://imgur.com/RUa4aSe.png";

    public String renderHtmlTextForSearchRequestEmail(List<SearchRequest> searchRequests, Set<ForumTopic> topics) {
        return html(
                body(
                        hr(),
                        img().withSrc(IMG_URL),
                        hr(),
                        generateTopicsContent(searchRequests, topics)
                )
        ).renderFormatted();
    }

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

    private DomContent generateTopicsContent(List<SearchRequest> searchRequests, Set<ForumTopic> topics) {
        return each(searchRequests, searchRequest ->
                p().with(
                        strong("According to your request: " + searchRequest.getRequest()),
                        iffElse(filter(topics, topic -> topic.getTitle().contains(searchRequest.getRequest())).isEmpty(), li("No results."),
                                each(filter(topics, topic -> topic.getTitle().contains(searchRequest.getRequest())),
                                        topic -> li(a(topic.getTitle()).withHref(getTopicUrl(topic)))
                                )
                        )
                )
        );
    }

    private String getTopicUrl(ForumTopic topic) {
        return HOST_URL + TOPIC_PATH + topic.getTopicForumId();
    }
}
