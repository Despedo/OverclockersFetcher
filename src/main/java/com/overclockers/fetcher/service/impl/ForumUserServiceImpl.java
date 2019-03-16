package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ForumUser;
import com.overclockers.fetcher.repository.ForumUserRepository;
import com.overclockers.fetcher.service.ForumUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ForumUserServiceImpl implements ForumUserService {

    private ForumUserRepository repository;

    @Override
    public ForumUser saveUser(ForumUser user) {
        ForumUser existing = repository.findUserByForumId(user.getUserForumId());
        return existing == null ? repository.save(user) : existing;
    }

}
