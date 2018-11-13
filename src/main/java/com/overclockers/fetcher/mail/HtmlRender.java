package com.overclockers.fetcher.mail;

import com.overclockers.fetcher.entity.ForumTopic;
import j2html.tags.DomContent;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.overclockers.fetcher.constants.OverclockersConstants.HOST_URL;
import static com.overclockers.fetcher.constants.OverclockersConstants.TOPIC_PATH;
import static j2html.TagCreator.*;

@Service
public class HtmlRender {

    private static final String IMG_URL = "https://imgur.com/RUa4aSe.png";

    public String renderHtmlTextForEmail(Set<String> searchList, Set<ForumTopic> topics) {
        return html(
                body(
                        hr(),
                        img().withSrc(IMG_URL),
                        hr(),
                        generateTopicsContent(searchList, topics)
                )
        ).render();
    }

    private DomContent generateTopicsContent(Set<String> searchSet, Set<ForumTopic> topics) {
        return each(searchSet, search ->
                p().with(
                        strong("According to your request: " + search),
                        iffElse(filter(topics, topic -> topic.getTitle().contains(search)).isEmpty(), li("No results."),
                                each(filter(topics, topic -> topic.getTitle().contains(search)),
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
