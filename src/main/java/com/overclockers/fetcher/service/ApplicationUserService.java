package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.ApplicationUser;

public interface ApplicationUserService {
    ApplicationUser findUserById(Long userId);

    ApplicationUser findUserByUserName(String userName);

    ApplicationUser findUserByEmail(String email);

    ApplicationUser saveUser(ApplicationUser user);

    void updateUserEmail(Long userId, String email);
}
