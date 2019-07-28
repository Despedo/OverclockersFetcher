package com.overclockers.fetcher.mail.impl;

import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.mail.HtmlRender;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class})
class HtmlRenderImplTest {

    HtmlRender htmlRender = new HtmlRenderImpl();

    @Test
    void renderHtmlTextForSearchRequestEmailTest() throws IOException {
        String expectedHtml = IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("mail/searchRequestEmail.html"),
                "UTF-8"
        );

        SearchRequest searchRequest = SearchRequest.builder().request("1080").build();
        ForumTopic firstTopic = new ForumTopic();
        firstTopic.setTitle("Продам 5шт MSI GTX 1080ti gaming x");
        firstTopic.setTopicForumId(46554L);
        ForumTopic secondTopic = new ForumTopic();
        secondTopic.setTitle("Видеокарта Manli GTX 1080Ti, укр гарантия");
        secondTopic.setTopicForumId(34545L);

        Map<SearchRequest, List<ForumTopic>> topicsMap = new HashMap<>();
        topicsMap.put(searchRequest, Arrays.asList(firstTopic, secondTopic));

        String renderedHtml = htmlRender.renderHtmlTextForSearchRequestEmail(topicsMap);

        assertThat(renderedHtml).isEqualToNormalizingNewlines(expectedHtml);
    }

    @Test
    void renderHtmlTextForRegistrationConfirmationTest() throws IOException {
        String expectedHtml = IOUtils.toString(
                this.getClass().getClassLoader().getResourceAsStream("mail/registrationConfirmation.html"),
                "UTF-8"
        );

        String renderedHtml = htmlRender.renderHtmlTextForRegistrationConfirmation("www.confirmation.com");

        assertThat(renderedHtml).isEqualToNormalizingNewlines(expectedHtml);
    }
}