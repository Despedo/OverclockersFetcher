package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.Topic;

import java.util.List;
import java.util.Set;

public interface TopicService {
    Topic saveOrUpdateTopic(Topic topic);

    void updateTopicsStatuses(Set<Topic> topicList, boolean isSent);

    List<Topic> findTopicsForSending(String searchTitle);
}
