package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.repository.ApplicationUserRepository;
import com.overclockers.fetcher.service.ApplicationUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.overclockers.fetcher.utils.DateTimeUtil.getCurrentTime;

@Service
@AllArgsConstructor
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private ApplicationUserRepository repository;
    private MailService mailService;

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

    @Override
    public void activateUser(ApplicationUser user, String password) {
        user.setPassword(password);
        user.setEnabled(true);
        user.setActivatedDateTime(getCurrentTime());
        repository.save(user);
    }

    @Override
    public ApplicationUser registerUser(String email, String firstName, String lastName, String registrationUrl) {
        ApplicationUser applicationUser = ApplicationUser.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .enabled(false)
                .confirmationToken(UUID.randomUUID().toString())
                .createdDateTime(getCurrentTime())
                .build();

        ApplicationUser savedUser = repository.save(applicationUser);
        mailService.processRegistrationEmail(applicationUser, registrationUrl);

        return savedUser;
    }
}
