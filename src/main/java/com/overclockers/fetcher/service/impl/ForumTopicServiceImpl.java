package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.repository.ForumTopicRepository;
import com.overclockers.fetcher.service.ForumTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class ForumTopicServiceImpl implements ForumTopicService {

    @Autowired
    private ForumTopicRepository repository;

    @Override
    public ForumTopic saveOrUpdateTopic(ForumTopic topic) {
        ForumTopic existing = repository.findTopicByForumId(topic.getTopicForumId());
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
    public void updateTopicsStatuses(Set<ForumTopic> topicSet, boolean isSent) {
        for (ForumTopic topic : topicSet) {
            repository.updateTopicsStatuses(topic.getTopicId(), isSent);
        }
    }

    @Override
    public List<ForumTopic> findTopicsForSending(String searchTitle) {
        return repository.findTopicsForSending(searchTitle);
    }
}
