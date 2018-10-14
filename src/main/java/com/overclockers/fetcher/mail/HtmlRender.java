package com.overclockers.fetcher.mail;

import com.overclockers.fetcher.entity.Topic;
import j2html.tags.DomContent;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.overclockers.fetcher.constants.OverclockersConstants.HOST_URL;
import static com.overclockers.fetcher.constants.OverclockersConstants.TOPIC_PATH;
import static j2html.TagCreator.*;

@Service
public class HtmlRender {

    private static final String IMG_URL = "https://imgur.com/RUa4aSe.png";

    public String renderHtmlTextForEmail(List<String> searchList, List<Topic> topics) {
        return html(
                body(
                        hr(),
                        img().withSrc(IMG_URL),
                        hr(),
                        generateTopicsContent(searchList, topics)
                )
        ).render();
    }

    private DomContent generateTopicsContent(List<String> searchList, List<Topic> topics) {
        return each(searchList, search ->
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

    private String getTopicUrl(Topic topic) {
        return HOST_URL + TOPIC_PATH + topic.getTopicForumId();
    }
}
