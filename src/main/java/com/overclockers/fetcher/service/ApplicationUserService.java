package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.ApplicationUser;

import java.util.List;

public interface ApplicationUserService {
    ApplicationUser findUserById(Long userId);

    ApplicationUser findUserByEmail(String email);

    ApplicationUser findUserByConfirmationToken(String confirmationToken);

    ApplicationUser saveUser(ApplicationUser user);

    List<ApplicationUser> findAllEnabledUsers();

    void updateUserEmail(Long userId, String email);
}
