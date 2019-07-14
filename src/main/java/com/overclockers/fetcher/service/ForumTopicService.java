package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ForumTopicService {
    ForumTopic saveTopic(ForumTopic topic);

    void saveTopics(Collection<ForumTopic> topics);

    void registerSentTopics(Collection<ForumTopic> forumTopics, ApplicationUser applicationUser);

    Map<SearchRequest, List<ForumTopic>> findTopicsMapForSending(List<SearchRequest> searchRequests, Long userId);

    List<ForumTopic> findTopicsByForumIds(Collection<Long> forumIds);

    ForumTopic findTopicByForumId(Long forumId);
}
