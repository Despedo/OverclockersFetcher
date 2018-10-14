package com.overclockers.fetcher.repository;

import com.overclockers.fetcher.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    @Query("FROM Topic t WHERE t.topicForumId = :topicForumId")
    Topic findByForumId(@Param("topicForumId") Long topicForumId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Topic t SET t.sentToUser = :isSent WHERE t.topicId = :topicId")
    void updateTopicsStatus(@Param("topicId") Long topicId, @Param("isSent") boolean isSent);

    @Query("FROM Topic t WHERE t.title like %:searchTitle% AND t.sentToUser = false")
    List<Topic> findAllByLikeTitle(@Param("searchTitle") String searchTitle);
}
