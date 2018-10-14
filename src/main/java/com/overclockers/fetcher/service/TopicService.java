package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.Topic;

import java.util.List;

public interface TopicService {
    Topic saveTopic(Topic topic);

    void updateTopicsStatus(List<Topic> topicList, boolean isSent);

    List<Topic> findTopicsByLikeTitle(String searchTitle);
}
