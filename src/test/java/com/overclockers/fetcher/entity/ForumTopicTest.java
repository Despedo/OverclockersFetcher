package com.overclockers.fetcher.entity;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ForumTopicTest {

    @Test
    void equalsTestFirstCaseTest() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        ForumTopic firstTopic = ForumTopic.builder()
                .id(2L)
                .topicForumId(2345L)
                .location("location2")
                .title("title2")
                .user(ForumUser.builder().build())
                .topicCreatedDateTime(dateTime)
                .topicUpdatedDateTime(dateTime)
                .createdDateTime(dateTime)
                .build();
        ForumTopic secondTopic = ForumTopic.builder()
                .id(2L)
                .topicForumId(2345L)
                .location("location2")
                .title("title2")
                .user(ForumUser.builder().build())
                .topicCreatedDateTime(dateTime)
                .topicUpdatedDateTime(dateTime)
                .createdDateTime(dateTime)
                .build();

        assertEquals(firstTopic, secondTopic);
    }

    @Test
    void equalsTestSecondCaseTest() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        ForumTopic firstTopic = ForumTopic.builder()
                .id(2L)
                .topicForumId(2345L)
                .location("location2")
                .title("title2")
                .user(ForumUser.builder().build())
                .topicCreatedDateTime(dateTime)
                .topicUpdatedDateTime(dateTime)
                .createdDateTime(dateTime)
                .build();
        ForumTopic secondTopic = ForumTopic.builder()
                .id(2L)
                .topicForumId(2345L)
                .location("location2")
                .title("title2")
                .user(ForumUser.builder().build())
                .topicCreatedDateTime(dateTime)
                .topicUpdatedDateTime(dateTime)
                .createdDateTime(dateTime.plusHours(1))
                .build();

        assertEquals(firstTopic, secondTopic);
    }

    @Test
    void equalsTestThirdCaseTest() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        ForumTopic firstTopic = ForumTopic.builder()
                .id(2L)
                .topicForumId(2345L)
                .location("location2")
                .title("title2")
                .user(ForumUser.builder().build())
                .topicCreatedDateTime(dateTime)
                .topicUpdatedDateTime(dateTime)
                .createdDateTime(dateTime)
                .build();
        ForumTopic secondTopic = ForumTopic.builder()
                .id(2L)
                .topicForumId(2345L)
                .location("location2")
                .title("title2")
                .user(ForumUser.builder().build())
                .topicCreatedDateTime(dateTime)
                .topicUpdatedDateTime(dateTime.plusHours(1))
                .createdDateTime(dateTime)
                .build();

        assertNotEquals(firstTopic, secondTopic);
    }

    @Test
    void equalsTestFourthCaseTest() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        ForumTopic firstTopic = ForumTopic.builder()
                .id(1L)
                .topicForumId(2345L)
                .location("location")
                .title("title")
                .user(ForumUser.builder().build())
                .topicCreatedDateTime(dateTime)
                .topicUpdatedDateTime(dateTime)
                .createdDateTime(dateTime)
                .build();
        ForumTopic secondTopic = ForumTopic.builder()
                .id(2L)
                .topicForumId(2345L)
                .location("location2")
                .title("title2")
                .user(ForumUser.builder().build())
                .topicCreatedDateTime(dateTime)
                .topicUpdatedDateTime(dateTime)
                .createdDateTime(dateTime)
                .build();

        assertNotEquals(firstTopic, secondTopic);
    }

    @Test
    void hashCodeConsistencyTest()
    {
        ZonedDateTime dateTime = ZonedDateTime.now();
        ForumTopic firstTopic = ForumTopic.builder()
                .id(1L)
                .topicForumId(2345L)
                .location("location")
                .title("title")
                .user(ForumUser.builder().build())
                .topicCreatedDateTime(dateTime)
                .topicUpdatedDateTime(dateTime)
                .createdDateTime(dateTime)
                .build();

        assertEquals(firstTopic.hashCode(), firstTopic.hashCode());
    }

    @Test
    void hashCodeEqualityTest()
    {
        ZonedDateTime dateTime = ZonedDateTime.now();
        ForumTopic firstTopic = ForumTopic.builder()
                .id(2L)
                .topicForumId(2345L)
                .location("location2")
                .title("title2")
                .user(ForumUser.builder().build())
                .topicCreatedDateTime(dateTime)
                .topicUpdatedDateTime(dateTime)
                .createdDateTime(dateTime)
                .build();
        ForumTopic secondTopic = ForumTopic.builder()
                .id(2L)
                .topicForumId(2345L)
                .location("location2")
                .title("title2")
                .user(ForumUser.builder().build())
                .topicCreatedDateTime(dateTime)
                .topicUpdatedDateTime(dateTime)
                .createdDateTime(dateTime.plusHours(1))
                .build();

        assertEquals(firstTopic, secondTopic);
        assertEquals(firstTopic.hashCode(), secondTopic.hashCode());
    }
}
