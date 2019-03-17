package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.repository.ApplicationUserRepository;
import com.overclockers.fetcher.service.ApplicationUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ApplicationUserServiceImpl implements ApplicationUserService {

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
    public List<ApplicationUser> findAllEnabledUsers() {
        return repository.findAllEnabledUsers();
    }

    @Override
    public void updateUserEmail(Long userId, String email) {
        repository.updateUserEmail(userId, email);
    }
}
