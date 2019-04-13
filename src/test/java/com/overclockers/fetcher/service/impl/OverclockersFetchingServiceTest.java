package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.parser.OverclockersParser;
import com.overclockers.fetcher.service.FetchingService;
import com.overclockers.fetcher.service.ForumTopicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
class OverclockersFetchingServiceTest {

    @Mock
    private OverclockersParser parser;
    @Mock
    private ForumTopicService topicService;

    private FetchingService fetchingService;

    @BeforeEach
    void init() {
        fetchingService = new OverclockersFetchingService(parser, topicService);
    }

    @Test
    void fetchAndSaveTopicsColdStartTest() {
        int coldStartFetchingSize = 10;
        List<ForumTopic> forumTopics = Arrays.asList(ForumTopic.builder().build(), ForumTopic.builder().build());
        when(parser.getForumTopics(anyString())).thenReturn(forumTopics);

        fetchingService.fetchAndSaveTopics();

        verify(parser, times(coldStartFetchingSize)).getForumTopics(anyString());
        verify(topicService, times(forumTopics.size() * coldStartFetchingSize)).saveTopic(any(ForumTopic.class));
    }

    @Test
    void fetchAndSaveTopicsHotStartTest() {
        int hotStartFetchingSize = 1;
        List<ForumTopic> forumTopics = Arrays.asList(ForumTopic.builder().build(), ForumTopic.builder().build());
        when(parser.getForumTopics(anyString())).thenReturn(forumTopics);
        ReflectionTestUtils.setField(fetchingService, "isColdStart", false);

        fetchingService.fetchAndSaveTopics();

        verify(parser, times(hotStartFetchingSize)).getForumTopics(anyString());
        verify(topicService, times(forumTopics.size() * hotStartFetchingSize)).saveTopic(any(ForumTopic.class));
    }
}