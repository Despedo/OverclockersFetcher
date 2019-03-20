package com.overclockers.fetcher;

import com.overclockers.fetcher.configuration.TestAppConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.overclockers.fetcher.Utils.getUserSession;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestAppConfiguration.class)
@WebAppConfiguration
public class LoginLogoutTest {
    private static final String TEST_USER_EMAIL = "Alex.W@gmail.com";
    private static final String TEST_USER_PASS = "The55rongPa55";
    private static final String DISABLED_TEST_USER_EMAIL = "John.P@gmail.com";
    private static final String DISABLED_TEST_USER_PASS = "Be77erAnd55Stronger";

    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();

        initMocks(this);
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void loginTest() throws Exception {
        mockMvc.perform(formLogin().user(TEST_USER_EMAIL).password(TEST_USER_PASS))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/request"))
                .andExpect(authenticated().withUsername(TEST_USER_EMAIL));
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void loginWithWrongPassTest() throws Exception {
        mockMvc.perform(formLogin().user(TEST_USER_EMAIL).password("wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"))
                .andExpect(unauthenticated());
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void loginWithDisabledUserTest() throws Exception {
        mockMvc.perform(formLogin().user(DISABLED_TEST_USER_EMAIL).password(DISABLED_TEST_USER_PASS))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"))
                .andExpect(unauthenticated());
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void logoutTest() throws Exception {
        mockMvc.perform(post("/logout").with(csrf())
                .session(getUserSession(mockMvc, TEST_USER_EMAIL, TEST_USER_PASS)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(unauthenticated());
    }
}
