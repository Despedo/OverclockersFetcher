package com.overclockers.fetcher.entity;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SentTopicTest {

    @Test
    void equalsTestFirstCaseTest() {
        SentTopic firstTopic = SentTopic.builder()
                .id(2L)
                .applicationUser(ApplicationUser.builder().id(1L).build())
                .forumTopic(ForumTopic.builder().id(2L).build())
                .build();
        SentTopic secondTopic = SentTopic.builder()
                .id(2L)
                .applicationUser(ApplicationUser.builder().id(1L).build())
                .forumTopic(ForumTopic.builder().id(2L).build())
                .build();

        assertEquals(firstTopic, secondTopic);
    }

    @Test
    void equalsTestFourthCaseTest() {
        SentTopic firstTopic = SentTopic.builder()
                .id(4L)
                .applicationUser(ApplicationUser.builder().id(34L).build())
                .forumTopic(ForumTopic.builder().id(2L).build())
                .build();
        SentTopic secondTopic = SentTopic.builder()
                .id(2L)
                .applicationUser(ApplicationUser.builder().id(1L).build())
                .forumTopic(ForumTopic.builder().id(2L).build())
                .build();

        assertNotEquals(firstTopic, secondTopic);
    }

    @Test
    void hashCodeConsistencyTest() {
        SentTopic firstTopic = SentTopic.builder()
                .id(4L)
                .applicationUser(ApplicationUser.builder().id(34L).build())
                .forumTopic(ForumTopic.builder().id(2L).build())
                .build();

        assertEquals(firstTopic.hashCode(), firstTopic.hashCode());
    }

    @Test
    void hashCodeEqualityTest() {
        SentTopic firstTopic = SentTopic.builder()
                .id(2L)
                .applicationUser(ApplicationUser.builder().id(1L).build())
                .forumTopic(ForumTopic.builder().id(2L).build())
                .build();
        SentTopic secondTopic = SentTopic.builder()
                .id(2L)
                .applicationUser(ApplicationUser.builder().id(1L).build())
                .forumTopic(ForumTopic.builder().id(2L).build())
                .build();

        assertEquals(firstTopic, secondTopic);
        assertEquals(firstTopic.hashCode(), secondTopic.hashCode());
    }

}