package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.*;
import com.overclockers.fetcher.repository.ForumTopicRepository;
import com.overclockers.fetcher.repository.SentTopicRepository;
import com.overclockers.fetcher.service.ForumTopicService;
import com.overclockers.fetcher.service.ForumUserService;
import com.overclockers.fetcher.service.RequestProcessor;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.overclockers.fetcher.utils.DateTimeUtil.getCurrentTime;

@Service
@AllArgsConstructor
public class ForumTopicServiceImpl implements ForumTopicService {

    private static final String ZERO_OR_MORE_REGEX = ".*";
    private static final String SINGLE_OR_NOT_REGEX = ".?";
    private static final String SPACE_CHAR = " ";
    private static final String ZERO_OR_MORE_LIKE = "%";
    private static final String SINGLE_SQL_LIKE = "_";

    @NonNull
    private ForumTopicRepository forumTopicRepository;
    @NonNull
    private SentTopicRepository sentTopicRepository;
    @NonNull
    private ForumUserService userService;
    @NonNull
    private EntityManager entityManager;
    @NonNull
    private RequestProcessor requestProcessor;

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

    // ToDo add search request to sent topics table. Possible issue when user adds new request that has matches already sent topic
    // ToDo add filtering to fetch topics only for last week
    @Transactional
    @Override
    public Map<SearchRequest, List<ForumTopic>> findTopicsMapForSending(List<SearchRequest> searchRequests, Long userId) {
        if (!searchRequests.isEmpty()) {
            Session session = entityManager.unwrap(Session.class);
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<ForumTopic> criteriaQuery = criteriaBuilder.createQuery(ForumTopic.class);

            Map<String, Set<String>> requestsPermutationsMap = requestProcessor.generateRequestsPermutationsMap(searchRequests);
            Set<String> allRequestsPermutations = requestsPermutationsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());

            Root<ForumTopic> root = criteriaQuery.from(ForumTopic.class);
            Predicate likeTitle = prepareOrLikeTitlePredicate(allRequestsPermutations, criteriaBuilder, root);
            Predicate notEqualToUserId = prepareNotEqualUserPredicate(userId, criteriaBuilder, root);
            criteriaQuery.select(root).where(criteriaBuilder.and(likeTitle, notEqualToUserId));
            Query<ForumTopic> query = session.createQuery(criteriaQuery);

            List<ForumTopic> forumTopics = query.getResultList();
            Map<SearchRequest, List<ForumTopic>> topicsMap = new HashMap<>();
            searchRequests.forEach(searchRequest -> {
                List<ForumTopic> matchedTopics = forumTopics.stream()
                        .filter(forumTopic -> isTopicMatchesToRequest(requestsPermutationsMap.get(searchRequest.getRequest()), forumTopic))
                        .collect(Collectors.toList());

                if (!matchedTopics.isEmpty()) {
                    topicsMap.put(searchRequest, matchedTopics);
                }
            });
            return topicsMap;
        } else {
            return Collections.emptyMap();
        }
    }

    boolean isTopicMatchesToRequest(Set<String> requests, ForumTopic forumTopic) {
        return requests.stream().anyMatch(s -> forumTopic.getTitle().toLowerCase().matches(getRequestMatchingRegex(s)));
    }

    private String getRequestMatchingRegex(String request) {
        return ZERO_OR_MORE_REGEX + request.toLowerCase().replaceAll(SPACE_CHAR, SINGLE_OR_NOT_REGEX) + ZERO_OR_MORE_REGEX;
    }

    private Predicate prepareNotEqualUserPredicate(Long userId, CriteriaBuilder criteriaBuilder, Root<ForumTopic> root) {
        Join<ForumTopic, SentTopic> joinTopic = root.join("sentTopic", JoinType.LEFT);
        Join<SentTopic, ApplicationUser> joinUser = joinTopic.join("applicationUser", JoinType.LEFT);
        return criteriaBuilder.or(criteriaBuilder.notEqual(joinUser.get("id"), userId), criteriaBuilder.isNull(joinUser.get("id")));
    }

    private Predicate prepareOrLikeTitlePredicate(Set<String> requestsPermutations, CriteriaBuilder criteriaBuilder, Root<ForumTopic> root) {
        Predicate[] likePredicates = requestsPermutations.stream()
                .map(request -> criteriaBuilder.like(root.get("title"),
                        ZERO_OR_MORE_LIKE + request.replaceAll(SPACE_CHAR, SINGLE_SQL_LIKE) + ZERO_OR_MORE_LIKE))
                .toArray(Predicate[]::new);
        return criteriaBuilder.or(likePredicates);
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
