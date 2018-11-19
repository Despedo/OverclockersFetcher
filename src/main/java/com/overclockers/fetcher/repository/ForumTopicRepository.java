package com.overclockers.fetcher.repository;

import com.overclockers.fetcher.entity.ForumTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumTopicRepository extends JpaRepository<ForumTopic, Long> {
    @Query("FROM ForumTopic t WHERE t.topicForumId = :topicForumId")
    ForumTopic findTopicByForumId(@Param("topicForumId") Long topicForumId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ForumTopic t SET t.sentToUser = :isSent WHERE t.topicId = :topicId")
    void updateTopicsStatuses(@Param("topicId") Long topicId, @Param("isSent") boolean isSent);

    @Query("FROM ForumTopic t WHERE t.title like %:searchTitle% AND t.sentToUser = false")
    List<ForumTopic> findTopicsForSending(@Param("searchTitle") String searchTitle);
}
