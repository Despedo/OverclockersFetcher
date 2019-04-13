package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ForumUser;
import com.overclockers.fetcher.repository.ForumUserRepository;
import com.overclockers.fetcher.service.ForumUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
class ForumUserServiceImplTest {

    @Mock
    private ForumUserRepository repository;

    private ForumUserService forumUserService;

    @BeforeEach
    void init() {
        forumUserService = new ForumUserServiceImpl(repository);
    }

    @Test
    void saveNewUserTest() {
        ForumUser newForumUser = ForumUser.builder().userForumId(1L).build();

        ForumUser savedUser = forumUserService.saveUser(newForumUser);

        verify(repository, times(1)).findUserByForumId(newForumUser.getUserForumId());
        verify(repository, times(1)).save(newForumUser);
        assertNotEquals(newForumUser, savedUser);
    }

    @Test
    void saveExistingUserTest() {
        ForumUser newForumUser = ForumUser.builder().userForumId(1L).build();
        when(repository.findUserByForumId(newForumUser.getUserForumId())).thenReturn(newForumUser);

        ForumUser savedUser = forumUserService.saveUser(newForumUser);

        verify(repository, times(1)).findUserByForumId(newForumUser.getUserForumId());
        verify(repository, times(0)).save(newForumUser);
        assertEquals(newForumUser, savedUser);
    }
}