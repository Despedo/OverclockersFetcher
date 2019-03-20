package com.overclockers.fetcher.authrization;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.service.ApplicationUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private ApplicationUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        ApplicationUser applicationUser = userService.findUserByEmail(username);
        return new ApplicationUserDetails(applicationUser);
    }
}
