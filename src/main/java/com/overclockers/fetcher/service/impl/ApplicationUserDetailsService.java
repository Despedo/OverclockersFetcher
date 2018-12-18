package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ApplicationUserDetails;
import com.overclockers.fetcher.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    @Autowired
    ApplicationUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser applicationUser = userService.findUserByEmail(username);
        return new ApplicationUserDetails(applicationUser);
    }
}
