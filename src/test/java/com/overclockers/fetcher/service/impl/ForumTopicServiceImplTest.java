package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.ForumUser;
import com.overclockers.fetcher.repository.ForumTopicRepository;
import com.overclockers.fetcher.repository.SentTopicRepository;
import com.overclockers.fetcher.service.ForumTopicService;
import com.overclockers.fetcher.service.ForumUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
class ForumTopicServiceImplTest {

    @Mock
    private ForumTopicRepository forumTopicRepository;
    @Mock
    private SentTopicRepository sentTopicRepository;
    @Mock
    private ForumUserService userService;

    private ForumTopicService forumTopicService;

    @BeforeEach
    void init() {
        forumTopicService = new ForumTopicServiceImpl(forumTopicRepository, sentTopicRepository, userService);
    }

    @Test
    void saveNewTopicTest() {
        ForumUser forumUser = ForumUser.builder().build();
        ForumTopic forumTopic = ForumTopic.builder().topicForumId(23523L).user(forumUser).build();
        when(userService.saveUser(forumUser)).thenReturn(forumUser);
        when(forumTopicRepository.save(forumTopic)).thenReturn(forumTopic);

        ForumTopic savedForumTopic = forumTopicService.saveTopic(forumTopic);

        verify(forumTopicRepository, times(1)).findTopicByForumId(forumTopic.getTopicForumId());
        verify(userService, times(1)).saveUser(forumUser);
        verify(forumTopicRepository, times(1)).save(forumTopic);
        assertNotNull(savedForumTopic.getCreatedDateTime());
        assertNotNull(savedForumTopic.getUser());
    }

    @Test
    void saveNewTopicsTest() {
        ForumUser forumUser = ForumUser.builder().id(1L).build();
        Collection<ForumTopic> topics = Arrays.asList(ForumTopic.builder().topicForumId(23523L).user(forumUser).build(),
                ForumTopic.builder().topicForumId(23523L).user(forumUser).build());

        forumTopicService.saveTopics(topics);

        verify(forumTopicRepository, times(1)).findTopicsByForumIds(anyCollection());
        verify(userService, times(1)).saveUsers(anyCollection());
        verify(forumTopicRepository, times(1)).saveAll(anyCollection());
    }

    @Test
    void saveNewTopicsWithEmptyCollectionTest() {
        Collection<ForumTopic> topics = Collections.emptyList();

        forumTopicService.saveTopics(topics);

        verify(forumTopicRepository, times(0)).findTopicsByForumIds(anyCollection());
        verify(userService, times(0)).saveUsers(anyCollection());
        verify(forumTopicRepository, times(0)).saveAll(anyCollection());
    }

    @Test
    void saveUpdatedTopicTest() {
        ForumUser forumUser = ForumUser.builder().build();
        ForumTopic newForumTopic = ForumTopic.builder().topicForumId(23523L).topicUpdatedDateTime(ZonedDateTime.now()).user(forumUser).build();
        ForumTopic updatedForumTopic = ForumTopic.builder().topicForumId(23523L).topicUpdatedDateTime(ZonedDateTime.now().plusHours(1)).user(forumUser).build();
        when(forumTopicRepository.findTopicByForumId(newForumTopic.getTopicForumId())).thenReturn(updatedForumTopic);
        when(forumTopicRepository.save(updatedForumTopic)).thenReturn(updatedForumTopic);

        ForumTopic savedForumTopic = forumTopicService.saveTopic(newForumTopic);

        verify(forumTopicRepository, times(1)).findTopicByForumId(newForumTopic.getTopicForumId());
        verify(userService, times(0)).saveUser(forumUser);
        verify(forumTopicRepository, times(1)).save(updatedForumTopic);
        assertEquals(updatedForumTopic, savedForumTopic);
    }

    @Test
    void saveExistingTopicTest() {
        ForumUser forumUser = ForumUser.builder().build();
        ForumTopic forumTopic = ForumTopic.builder().topicForumId(23523L).user(forumUser).build();
        when(forumTopicRepository.findTopicByForumId(forumTopic.getTopicForumId())).thenReturn(forumTopic);

        ForumTopic savedForumTopic = forumTopicService.saveTopic(forumTopic);

        verify(forumTopicRepository, times(1)).findTopicByForumId(forumTopic.getTopicForumId());
        verify(userService, times(0)).saveUser(any(ForumUser.class));
        verify(forumTopicRepository, times(0)).save(any(ForumTopic.class));
        assertEquals(forumTopic, savedForumTopic);
    }

    @Test
    void registerSentTopicsTest() {
        ApplicationUser applicationUser = ApplicationUser.builder().build();
        List<ForumTopic> forumTopics = Arrays.asList(ForumTopic.builder().build());

        forumTopicService.registerSentTopics(forumTopics, applicationUser);

        verify(sentTopicRepository, times(1)).saveAll(anyList());
    }

    @Test
    void findTopicsForSendingTest() {
        String searchTitle = "1080";
        Long userId = 324L;

        forumTopicService.findTopicsForSending(searchTitle, userId);

        verify(forumTopicRepository, times(1)).findTopicsForSending(searchTitle, userId);
    }
}