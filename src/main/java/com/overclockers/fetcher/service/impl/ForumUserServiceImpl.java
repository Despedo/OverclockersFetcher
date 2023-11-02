package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ForumUser;
import com.overclockers.fetcher.repository.ForumUserRepository;
import com.overclockers.fetcher.service.ForumUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.overclockers.fetcher.utils.DateTimeUtil.getCurrentTime;

@Service
@AllArgsConstructor
public class ForumUserServiceImpl implements ForumUserService {

    private final ForumUserRepository repository;

    @Override
    public ForumUser saveUser(ForumUser user) {
        ForumUser persistedUser = repository.findUserByForumId(user.getUserForumId());
        if (persistedUser == null) {
            user.setCreatedDateTime(getCurrentTime());
            return repository.save(user);
        } else {
            return persistedUser;
        }
    }

    @Override
    public void saveUsers(Collection<ForumUser> users) {
        if (!users.isEmpty()) {
            List<ForumUser> persistedUsers = repository.findUsersByForumIds(getForumIds(users));

            List<ForumUser> newUsers = getNewUsers(users, persistedUsers);
            repository.saveAll(newUsers);

            Map<Long, ForumUser> allUsersMap = getAllUserMap(persistedUsers, newUsers);

            users.forEach(user -> {
                ForumUser persistedUser = allUsersMap.get(user.getUserForumId());
                user.setId(persistedUser.getId());
                user.setCreatedDateTime(persistedUser.getCreatedDateTime());
            });
        }
    }

    private Map<Long, ForumUser> getAllUserMap(List<ForumUser> persistedUsers, List<ForumUser> newUsers) {
        Map<Long, ForumUser> allUsersMap = new HashMap<>();
        allUsersMap.putAll(getForumUsersMap(persistedUsers));
        allUsersMap.putAll(getForumUsersMap(newUsers));
        return allUsersMap;
    }

    private List<ForumUser> getNewUsers(Collection<ForumUser> users, List<ForumUser> persistedUsers) {
        List<ForumUser> newUsers = new ArrayList<>(users);
        newUsers.removeAll(persistedUsers);
        newUsers.forEach(user -> user.setCreatedDateTime(getCurrentTime()));
        return newUsers;
    }

    private Map<Long, ForumUser> getForumUsersMap(Collection<ForumUser> users) {
        return users.stream().collect(Collectors.toMap(ForumUser::getUserForumId, user -> user));
    }

    private List<Long> getForumIds(Collection<ForumUser> users) {
        return users.stream().map(ForumUser::getUserForumId).collect(Collectors.toList());
    }

}
