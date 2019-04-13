package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;

import java.util.List;

public interface ForumTopicService {
    ForumTopic saveTopic(ForumTopic topic);

    void registerSentTopics(List<ForumTopic> forumTopics, ApplicationUser applicationUser);

    List<ForumTopic> findTopicsForSending(String searchTitle, Long userId);
}
