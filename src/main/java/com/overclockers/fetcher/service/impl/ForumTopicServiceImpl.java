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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        ForumTopic existingTopic = findTopicByForumId(topic.getTopicForumId());
        if (existingTopic == null) {
            ForumUser savedUser = userService.saveUser(topic.getUser());
            topic.setUser(savedUser);
            topic.setCreatedDateTime(getCurrentTime());
            return forumTopicRepository.save(topic);
        } else if (isTopicUpdated(topic, existingTopic)) {
            topic.setUser(existingTopic.getUser());
            return forumTopicRepository.save(existingTopic);
        } else {
            return existingTopic;
        }
    }

    @Transactional
    @Override
    public void saveTopics(Collection<ForumTopic> topics) {
        if (!topics.isEmpty()) {
            List<ForumTopic> persistedTopics = findTopicsByForumIds(getForumIds(topics));
            topics.removeAll(persistedTopics);

            Map<Long, ForumUser> usersMap = getForumUsersMap(topics);
            userService.saveUsers(usersMap.values());
            topics.forEach(topic -> {
                topic.setCreatedDateTime(getCurrentTime());
                topic.setUser(usersMap.get(topic.getUser().getUserForumId()));
            });
            forumTopicRepository.saveAll(topics);
        }
    }

    private List<Long> getForumIds(Collection<ForumTopic> topics) {
        return topics.stream().map(ForumTopic::getTopicForumId).collect(Collectors.toList());
    }

    private Map<Long, ForumUser> getForumUsersMap(Collection<ForumTopic> topics) {
        return topics.stream().collect(Collectors.toMap(forumTopic -> forumTopic.getUser().getUserForumId(), ForumTopic::getUser, (id1, id2) -> id1));
    }

    private boolean isTopicUpdated(ForumTopic topic, ForumTopic existing) {
        return existing.getTopicUpdatedDateTime() != null && topic.getTopicUpdatedDateTime() != null &&
                !existing.getTopicUpdatedDateTime().isEqual(topic.getTopicUpdatedDateTime());
    }

    @Override
    public void registerSentTopics(Collection<ForumTopic> forumTopics, ApplicationUser applicationUser) {
        if (!forumTopics.isEmpty()) {
            List<SentTopic> sentTopics = forumTopics.stream().map
                    (topic -> SentTopic.builder()
                            .applicationUser(applicationUser)
                            .forumTopic(topic)
                            .createdDatetime(getCurrentTime())
                            .build()
                    ).collect(Collectors.toList());

            sentTopicRepository.saveAll(sentTopics);
        }
    }

    @Override
    public List<ForumTopic> findTopicsForSending(String searchTitle, Long userId) {
        return forumTopicRepository.findTopicsForSending(searchTitle, userId);
    }

    @Override
    public List<ForumTopic> findTopicsByForumIds(Collection<Long> forumIds) {
        return forumIds.isEmpty() ? Collections.emptyList() : forumTopicRepository.findTopicsByForumIds(forumIds);
    }

    @Override
    public ForumTopic findTopicByForumId(Long forumId) {
        return forumTopicRepository.findTopicByForumId(forumId);
    }
}
