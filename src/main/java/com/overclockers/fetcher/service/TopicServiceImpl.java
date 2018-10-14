package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.Topic;
import com.overclockers.fetcher.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepository repository;

    @Override
    public Topic saveTopic(Topic topic) {
        Topic existing = repository.findByForumId(topic.getTopicForumId());
        return existing == null ? repository.save(topic) : existing;
    }

    @Transactional
    @Override
    public void updateTopicsStatus(List<Topic> topicList, boolean isSent) {
        for (Topic topic : topicList) {
            repository.updateTopicsStatus(topic.getTopicId(), isSent);
        }
    }

    @Override
    public List<Topic> findTopicsByLikeTitle(String searchTitle) {
        return repository.findAllByLikeTitle(searchTitle);
    }
}
