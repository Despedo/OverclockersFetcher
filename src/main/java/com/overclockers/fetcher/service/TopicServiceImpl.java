package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.Topic;
import com.overclockers.fetcher.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepository repository;

    @Override
    public Topic saveOrUpdateTopic(Topic topic) {
        Topic existing = repository.findTopicByForumId(topic.getTopicForumId());
        if (existing == null) {
            return repository.save(topic);
        } else if(!existing.equals(topic)){
            topic.setTopicId(existing.getTopicId());
            topic.setSentToUser(false);
            return repository.save(topic);
        }
        else {
            return existing;
        }
    }

    @Transactional
    @Override
    public void updateTopicsStatuses(Set<Topic> topicSet, boolean isSent) {
        for (Topic topic : topicSet) {
            repository.updateTopicsStatuses(topic.getTopicId(), isSent);
        }
    }

    @Override
    public List<Topic> findTopicsForSending(String searchTitle) {
        return repository.findTopicsForSending(searchTitle);
    }
}
