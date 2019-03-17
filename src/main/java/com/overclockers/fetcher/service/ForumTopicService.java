package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.ForumTopic;

import java.util.List;
import java.util.Set;

public interface ForumTopicService {
    ForumTopic saveOrUpdateTopic(ForumTopic topic);

    void updateTopicsStatuses(Set<ForumTopic> topicList, boolean isSent);

    List<ForumTopic> findTopicsByTitle(String searchTitle);
}
