package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SentTopic;
import com.overclockers.fetcher.repository.ForumTopicRepository;
import com.overclockers.fetcher.repository.SentTopicRepository;
import com.overclockers.fetcher.service.ForumTopicService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.overclockers.fetcher.utils.DateTimeUtil.getCurrentTime;

@Service
@AllArgsConstructor
public class ForumTopicServiceImpl implements ForumTopicService {

    private ForumTopicRepository forumTopicRepository;
    private SentTopicRepository sentTopicRepository;

    @Transactional
    @Override
    public ForumTopic saveOrUpdateTopic(ForumTopic topic) {
        ForumTopic existing = forumTopicRepository.findTopicByForumId(topic.getTopicForumId());
        if (existing == null) {
            return forumTopicRepository.save(topic);
        } else if (!existing.equals(topic)) {
            topic.setId(existing.getId());
            //Todo change to only saving, we need to save new topic instead updating to avoid issues when topic was sent to user
            return forumTopicRepository.save(topic);
        } else {
            return existing;
        }
    }

    @Transactional
    @Override
    public void registerSentTopics(List<ForumTopic> forumTopics, ApplicationUser applicationUser) {
        List<SentTopic> sentTopics = new ArrayList<>();
        for (ForumTopic topic : forumTopics) {
            sentTopics.add(SentTopic.builder()
                    .applicationUser(applicationUser)
                    .forumTopic(topic)
                    .createdDatetime(getCurrentTime())
                    .build());
        }
        sentTopicRepository.saveAll(sentTopics);
    }

    @Override
    public List<ForumTopic> findTopicsForSending(String searchTitle, Long userId) {
        return forumTopicRepository.findTopicsForSending(searchTitle, userId);
    }
}
