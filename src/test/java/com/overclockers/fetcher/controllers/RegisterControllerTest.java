package com.overclockers.fetcher.controllers;

import com.overclockers.fetcher.configuration.TestAppConfiguration;
import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.service.ApplicationUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static com.overclockers.fetcher.Utils.buildUrlEncodedFormEntity;
import static com.overclockers.fetcher.constants.ControllerConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestAppConfiguration.class)
@WebAppConfiguration
class RegisterControllerTest {

    private static final String FIRST_NAME_FILED = "firstName";
    private static final String LAST_NAME_FILED = "lastName";
    private static final String EMAIL_FIELD = "email";

    @Spy
    @Autowired
    private ApplicationUserService applicationUserService;
    @Spy
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private MailService mailService;

    @Autowired
    @InjectMocks
    private RegisterController registerController;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setSuffix(".html");

        this.mockMvc = MockMvcBuilders.standaloneSetup(registerController)
                .setViewResolvers(viewResolver)
                .build();

        initMocks(this);
    }

    @Test
    void showLoginPageTest() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name(LOGIN_VIEW))
                .andExpect(model().attribute(LOGIN_ERROR_ATTRIBUTE, false));
    }

    @Test
    void showRegistrationPageTest() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name(REGISTER_VIEW))
                .andExpect(model().attributeExists(USER_ATTRIBUTE))
                .andExpect(model().attributeHasNoErrors(USER_ATTRIBUTE));
    }

    @Test
    void processRegistrationFormWithUserValidationTest() throws Exception {
        String firstName = "";
        String lastName = "";
        String email = "test";

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(buildUrlEncodedFormEntity(
                        FIRST_NAME_FILED, firstName,
                        LAST_NAME_FILED, lastName,
                        EMAIL_FIELD, email
                )))
                .andExpect(status().isOk())
                .andExpect(view().name(REGISTER_VIEW))
                .andExpect(model().attributeExists(USER_ATTRIBUTE))
                .andExpect(model().attributeHasFieldErrors(USER_ATTRIBUTE, FIRST_NAME_FILED))
                .andExpect(model().attributeHasFieldErrors(USER_ATTRIBUTE, LAST_NAME_FILED))
                .andExpect(model().attributeHasFieldErrors(USER_ATTRIBUTE, EMAIL_FIELD));
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void processRegistrationFormWithExistingUserTest() throws Exception {
        String firstName = "Nick";
        String lastName = "Wilson";
        String email = "Alex.W@gmail.com";

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(buildUrlEncodedFormEntity(
                        FIRST_NAME_FILED, firstName,
                        LAST_NAME_FILED, lastName,
                        EMAIL_FIELD, email
                )))
                .andExpect(status().isOk())
                .andExpect(view().name(REGISTER_VIEW))
                .andExpect(model().attribute(ERROR_MESSAGE_ATTRIBUTE, "Oops!  There is already a user registered with the email provided."))
                .andExpect(model().attributeExists(USER_ATTRIBUTE))
                .andExpect(model().attributeHasErrors(USER_ATTRIBUTE));

        verify(applicationUserService).findUserByEmail(email);
        verify(applicationUserService, never()).saveUser(any(ApplicationUser.class));
        verify(mailService, never()).processRegistrationEmail(any(ApplicationUser.class), eq("http://localhost:80"));
    }

    @Test
    void processRegistrationFormWithNewUserTest() throws Exception {
        String firstName = "Nick";
        String lastName = "Wilson";
        String email = "test2@gmail.com";

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(buildUrlEncodedFormEntity(
                        FIRST_NAME_FILED, firstName,
                        LAST_NAME_FILED, lastName,
                        EMAIL_FIELD, email
                )))
                .andExpect(status().isOk())
                .andExpect(view().name(REGISTER_VIEW))
                .andExpect(model().attribute(CONFIRMATION_MESSAGE_ATTRIBUTE, "A confirmation e-mail has been sent to " + email))
                .andExpect(model().attributeExists(USER_ATTRIBUTE))
                .andExpect(model().attributeHasNoErrors(USER_ATTRIBUTE));

        verify(applicationUserService).findUserByEmail(email);
        verify(applicationUserService).registerUser(email, firstName, lastName, "http://localhost:80");
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void showConfirmationPageForActivatedUserTest() throws Exception {
        String token = "7cf9437b-15c3-4705-8923-6a68a3894032";

        mockMvc.perform(get("/confirm")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name(CONFIRM_VIEW))
                .andExpect(model().attribute(ERROR_MESSAGE_ATTRIBUTE, "Oops!  User is already activated." + "\n" + "22.01.2019 23:55"));

        verify(applicationUserService).findUserByConfirmationToken(token);
    }

    @Test
    void showConfirmationPageWithWrongTokenTest() throws Exception {
        String token = "7cf9437b-15c3-4705-8923-6a68a3894562";

        mockMvc.perform(get("/confirm")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name(CONFIRM_VIEW))
                .andExpect(model().attribute(ERROR_MESSAGE_ATTRIBUTE, "Oops!  This is an invalid confirmation link."));

        verify(applicationUserService).findUserByConfirmationToken(token);
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void showConfirmationPageForNotActivatedUserTest() throws Exception {
        String token = "5f899c89-0989-9080-0979-0-98a098c787";

        mockMvc.perform(get("/confirm")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name(CONFIRM_VIEW))
                .andExpect(model().attribute(CONFIRMATION_TOKEN_ATTRIBUTE, token));

        verify(applicationUserService).findUserByConfirmationToken(token);
    }


    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void processConfirmationPageForActivatedUserTest() throws Exception {
        String token = "7cf9437b-15c3-4705-8923-6a68a3894032";

        mockMvc.perform(post("/confirm")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name(CONFIRM_VIEW))
                .andExpect(model().attribute(ERROR_MESSAGE_ATTRIBUTE, "Oops!  User is already activated." + "\n" + "22.01.2019 23:55"));

        verify(applicationUserService).findUserByConfirmationToken(token);
        verify(applicationUserService, never()).saveUser(any(ApplicationUser.class));
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void processConfirmationPageWithWrongTokenTest() throws Exception {
        String token = "7cf9437b-15c3-4705-8923-6a68a3894562";

        mockMvc.perform(post("/confirm")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name(CONFIRM_VIEW))
                .andExpect(model().attribute(ERROR_MESSAGE_ATTRIBUTE, "Oops!  This is an invalid confirmation link."));

        verify(applicationUserService).findUserByConfirmationToken(token);
        verify(applicationUserService, never()).saveUser(any(ApplicationUser.class));
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void processConfirmationFormForNotActivatedUserTest() throws Exception {
        String token = "5f899c89-0989-9080-0979-0-98a098c787";
        String password = "life_is_good";

        mockMvc.perform(post("/confirm")
                .param("token", token)
                .param("password", password))
                .andExpect(status().isOk())
                .andExpect(view().name(CONFIRM_VIEW))
                .andExpect(model().attribute(SUCCESS_MESSAGE_ATTRIBUTE, "Your password has been set!"));


        verify(applicationUserService).findUserByConfirmationToken(token);
        verify(bCryptPasswordEncoder).encode(password);
        verify(applicationUserService).activateUser(any(ApplicationUser.class), anyString());
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void processConfirmationFormWithWeakPassTest() throws Exception {
        String token = "5f899c89-0989-9080-0979-0-98a098c787";
        String password = "lif";

        mockMvc.perform(post("/confirm")
                .param("token", token)
                .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + CONFIRM_VIEW + "?token=" + token))
                .andExpect(model().attribute(ERROR_MESSAGE_ATTRIBUTE, "Your password is too weak.  Choose a stronger one."));

        verify(applicationUserService).findUserByConfirmationToken(token);
        verify(bCryptPasswordEncoder, never()).encode(password);
        verify(applicationUserService, never()).saveUser(any(ApplicationUser.class));
    }


}