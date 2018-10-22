package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.Topic;

import java.util.List;

public interface TopicService {
    Topic saveOrUpdateTopic(Topic topic);

    void updateTopicsStatuses(List<Topic> topicList, boolean isSent);

    List<Topic> findTopicsForSending(String searchTitle);
}
