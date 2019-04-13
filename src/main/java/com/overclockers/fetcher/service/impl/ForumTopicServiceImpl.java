package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.ForumUser;
import com.overclockers.fetcher.entity.SentTopic;
import com.overclockers.fetcher.repository.ForumTopicRepository;
import com.overclockers.fetcher.repository.SentTopicRepository;
import com.overclockers.fetcher.service.ForumTopicService;
import com.overclockers.fetcher.service.ForumUserService;
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
    private ForumUserService userService;

    @Transactional
    @Override
    public ForumTopic saveTopic(ForumTopic topic) {
        ForumTopic existingTopic = forumTopicRepository.findTopicByForumId(topic.getTopicForumId());
        if (existingTopic == null) {
            ForumUser user = topic.getUser();
            user.setCreatedDateTime(getCurrentTime());
            topic.setUser(userService.saveUser(user));
            topic.setCreatedDateTime(getCurrentTime());
            return forumTopicRepository.save(topic);
        } else if (isTopicUpdated(topic, existingTopic)) {
            topic.setUser(existingTopic.getUser());
            return forumTopicRepository.save(existingTopic);
        } else {
            return existingTopic;
        }
    }

    private boolean isTopicUpdated(ForumTopic topic, ForumTopic existing) {
        return existing.getTopicUpdatedDateTime() != null && topic.getTopicUpdatedDateTime() != null &&
                !existing.getTopicUpdatedDateTime().isEqual(topic.getTopicUpdatedDateTime());
    }

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
