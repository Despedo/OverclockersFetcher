package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;

import java.util.Collection;
import java.util.List;

public interface ForumTopicService {
    ForumTopic saveTopic(ForumTopic topic);

    void saveTopics(Collection<ForumTopic> topics);

    void registerSentTopics(Collection<ForumTopic> forumTopics, ApplicationUser applicationUser);

    List<ForumTopic> findTopicsForSending(String searchTitle, Long userId);

    List<ForumTopic> findTopicsByForumIds(Collection<Long> forumIds);

    ForumTopic findTopicByForumId(Long forumId);
}
