package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.User;
import com.overclockers.fetcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public User saveUser(User user) {
        User existing = repository.findUserByForumId(user.getUserForumId());
        return existing == null ? repository.save(user) : existing;
    }

}
