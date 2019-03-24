package com.overclockers.fetcher.repository;

import com.overclockers.fetcher.entity.SentTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentTopicRepository extends JpaRepository<SentTopic, Long> {
    @Query("FROM SentTopic t WHERE t.applicationUser = :userId")
    List<SentTopic> findSentTopicsByApplicationUserId(@Param("userId") Long userId);
}
