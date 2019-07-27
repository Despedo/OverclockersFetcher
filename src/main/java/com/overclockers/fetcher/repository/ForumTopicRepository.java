package com.overclockers.fetcher.repository;

import com.overclockers.fetcher.entity.ForumTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ForumTopicRepository extends JpaRepository<ForumTopic, Long> {
    @Query("FROM ForumTopic t WHERE t.topicForumId = :topicForumId")
    ForumTopic findTopicByForumId(@Param("topicForumId") Long topicForumId);

    @Query("FROM ForumTopic t WHERE t.topicForumId IN :forumIds")
    List<ForumTopic> findTopicsByForumIds(@Param("forumIds") Collection<Long> forumIds);
}
