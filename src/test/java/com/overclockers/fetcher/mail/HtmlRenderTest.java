package com.overclockers.fetcher.mail;

import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.mail.impl.HtmlRenderImpl;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlRenderTest {

    HtmlRender htmlRenderImpl = new HtmlRenderImpl();

    @Test
    void renderHtmlTextForSearchRequestEmail() throws IOException {
        String expectedHtml = IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("mail/searchRequestEmail.html"),
                "UTF-8"
        );

        List<SearchRequest> searchRequests = new ArrayList<>(Arrays.asList(SearchRequest.builder().request("1080").build()));
        ForumTopic firstTopic = new ForumTopic();
        firstTopic.setTitle("Продам 5шт MSI GTX 1080ti gaming x");
        firstTopic.setTopicForumId(46554L);
        ForumTopic secondTopic = new ForumTopic();
        secondTopic.setTitle("Видеокарта Manli GTX 1080Ti, укр гарантия");
        secondTopic.setTopicForumId(34545L);
        List<ForumTopic> topics = Arrays.asList(firstTopic, secondTopic);

        String renderedHtml = htmlRenderImpl.renderHtmlTextForSearchRequestEmail(searchRequests, topics);

        assertEquals(expectedHtml, renderedHtml);
    }

    @Test
    void renderHtmlTextForRegistrationConfirmation() throws IOException {
        String expectedHtml = IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("mail/registrationConfirmation.html"),
                "UTF-8"
        );

        String renderedHtml = htmlRenderImpl.renderHtmlTextForRegistrationConfirmation("www.confirmation.com");

        assertEquals(expectedHtml, renderedHtml);
    }
}