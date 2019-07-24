package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.repository.ApplicationUserRepository;
import com.overclockers.fetcher.service.ApplicationUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
class StartWebApplicationUserServiceImplTest {
    @Mock
    private ApplicationUserRepository repository;
    @Mock
    private MailService mailService;

    private ApplicationUserService applicationUserService;

    @BeforeEach
    void init() {
        applicationUserService = new ApplicationUserServiceImpl(repository, mailService);
    }

    @Test
    void findUserByIdTest() {
        Long userId = 234L;
        ApplicationUser applicationUser = ApplicationUser.builder().id(userId).build();
        when(repository.findUserById(userId)).thenReturn(applicationUser);

        ApplicationUser foundedUser = applicationUserService.findUserById(userId);

        verify(repository, times(1)).findUserById(userId);
        assertEquals(applicationUser, foundedUser);
    }

    @Test
    void findUserByEmailTest() {
        String email = "test@mail.com";
        ApplicationUser applicationUser = ApplicationUser.builder().email(email).build();
        when(repository.findUserByEmail(email)).thenReturn(applicationUser);

        ApplicationUser foundedUser = applicationUserService.findUserByEmail(email);

        verify(repository, times(1)).findUserByEmail(email);
        assertEquals(applicationUser, foundedUser);
    }

    @Test
    void findUserByConfirmationTokenTest() {
        String token = "51a2c940-fdf9-4b47-9439-26677c54dd80";
        ApplicationUser applicationUser = ApplicationUser.builder().confirmationToken(token).build();
        when(repository.findUserByConfirmationToken(token)).thenReturn(applicationUser);

        ApplicationUser foundedUser = applicationUserService.findUserByConfirmationToken(token);

        verify(repository, times(1)).findUserByConfirmationToken(token);
        assertEquals(applicationUser, foundedUser);
    }

    @Test
    void saveUserTest() {
        ApplicationUser applicationUser = ApplicationUser.builder().build();
        when(repository.save(applicationUser)).thenReturn(applicationUser);

        ApplicationUser savedUser = applicationUserService.saveUser(applicationUser);

        verify(repository, times(1)).save(applicationUser);
        assertEquals(applicationUser, savedUser);
    }

    @Test
    void findAllEnabledUsersTest() {
        List<ApplicationUser> enabledUsers = Collections.singletonList(ApplicationUser.builder().build());
        when(repository.findAllEnabledUsers()).thenReturn(enabledUsers);

        List<ApplicationUser> foundedEnabledUsers = applicationUserService.findAllEnabledUsers();

        verify(repository, times(1)).findAllEnabledUsers();
        assertEquals(enabledUsers, foundedEnabledUsers);
    }

    @Test
    void updateUserEmailTest() {
        Long userId = 234L;
        String email = "test@mail.com";

        applicationUserService.updateUserEmail(userId, email);

        verify(repository).updateUserEmail(userId, email);
    }

    @Test
    void activateUserTest() {
        ApplicationUser applicationUser = ApplicationUser.builder().build();

        applicationUserService.activateUser(applicationUser, "dsfd");

        verify(repository).save(any(ApplicationUser.class));
    }

    @Test
    void registerUser() {
        String email = "test@mail.com";
        String firstName = "Alex";
        String lastName = "Jenks";
        String registrationUrl = "url";
        ApplicationUser userForRegistration = ApplicationUser.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        when(repository.save(any(ApplicationUser.class))).thenReturn(userForRegistration);

        ApplicationUser registeredUser = applicationUserService.registerUser(email, firstName, lastName, registrationUrl);

        verify(repository, times(1)).save(any(ApplicationUser.class));
        verify(mailService, times(1)).processRegistrationEmail(any(ApplicationUser.class), eq(registrationUrl));
        assertEquals(userForRegistration, registeredUser);
    }

}