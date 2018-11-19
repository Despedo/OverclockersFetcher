package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.repository.ApplicationUserRepository;
import com.overclockers.fetcher.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserServiceImpl implements ApplicationUserService {

    @Autowired
    private ApplicationUserRepository repository;

    @Override
    public ApplicationUser findUserById(Long userId) {
        return repository.findUserById(userId);
    }

    @Override
    public ApplicationUser findUserByEmail(String email) {
        return repository.findUserByEmail(email);
    }

    @Override
    public ApplicationUser findUserByConfirmationToken(String confirmationToken) {
        return repository.findUserByConfirmationToken(confirmationToken);
    }

    @Override
    public ApplicationUser saveUser(ApplicationUser user) {
        return repository.save(user);
    }

    @Override
    public void updateUserEmail(Long userId, String email) {
        repository.updateUserEmail(userId, email);
    }
}
