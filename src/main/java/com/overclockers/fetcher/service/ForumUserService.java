package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.ForumUser;

import java.util.Collection;

public interface ForumUserService {
    ForumUser saveUser(ForumUser user);

    void saveUsers(Collection<ForumUser> users);
}
