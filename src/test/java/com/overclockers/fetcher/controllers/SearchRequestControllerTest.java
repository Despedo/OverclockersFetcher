package com.overclockers.fetcher.controllers;

import com.overclockers.fetcher.configuration.TestAppConfiguration;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.service.ApplicationUserService;
import com.overclockers.fetcher.service.SearchRequestService;
import com.overclockers.fetcher.utils.SearchRequestConverter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.overclockers.fetcher.Utils.buildUrlEncodedFormEntity;
import static com.overclockers.fetcher.Utils.getUserSession;
import static com.overclockers.fetcher.constants.ControllerConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestAppConfiguration.class)
@WebAppConfiguration
@Disabled
class SearchRequestControllerTest {
    private static final String TEST_USER_EMAIL = "Alex.W@gmail.com";
    private static final String TEST_USER_PASS = "The55rongPa55";

    @Mock
    private Appender mockAppender;
    @Captor
    private ArgumentCaptor<LogEvent> captorLoggingEvent;
    private Logger logger;

    @Spy
    @Autowired
    private SearchRequestConverter requestConverter;
    @Spy
    @Autowired
    private SearchRequestService searchRequestService;
    @Spy
    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    @InjectMocks
    private SearchRequestController requestController;

    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();

        initMocks(this);
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void showMainPageTest() throws Exception {
        mockMvc.perform(get("/")
                .session(getUserSession(mockMvc, TEST_USER_EMAIL, TEST_USER_PASS)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/request"))
                .andExpect(view().name(REDIRECT + REQUEST_VIEW));
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void processRequestWithLoggedUserTest() throws Exception {
        mockMvc.perform(post("/request").with(csrf())
                .session(getUserSession(mockMvc, TEST_USER_EMAIL, TEST_USER_PASS))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(buildUrlEncodedFormEntity(
                        SEARCH_REQUEST_ATTRIBUTE, "ASUS MB"
                )))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/request"))
                .andExpect(view().name(REDIRECT + REQUEST_VIEW));

        verify(applicationUserService).findUserByEmail(TEST_USER_EMAIL);
        verify(searchRequestService).saveSearchRequest(any(SearchRequest.class));
    }

    @Sql(value = {"classpath:sql/createApplicationUsers.sql", "classpath:sql/createUserRequests.sql"}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void showRequestsTest() throws Exception {
        mockMvc.perform(get("/request")
                .session(getUserSession(mockMvc, TEST_USER_EMAIL, TEST_USER_PASS)))
                .andExpect(status().isOk())
                .andExpect(view().name(REQUEST_VIEW))
                .andExpect(model().attributeExists(SEARCH_REQUEST_ATTRIBUTE))
                .andExpect(model().attribute(EMPTY_MESSAGE_ATTRIBUTE, "No requests"));

        verify(requestConverter).convertSearchRequests(anyList());
    }

    @Sql(value = "classpath:sql/createApplicationUsers.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void processEmptyRequestTest() throws Exception {
        setupLogger(SearchRequestController.class, Level.INFO);

        mockMvc.perform(post("/request").with(csrf())
                .session(getUserSession(mockMvc, TEST_USER_EMAIL, TEST_USER_PASS))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(buildUrlEncodedFormEntity(
                        SEARCH_REQUEST_ATTRIBUTE, ""
                )))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/request"))
                .andExpect(view().name(REDIRECT + REQUEST_VIEW));

        verify(applicationUserService, never()).findUserByEmail(TEST_USER_EMAIL);
        verify(searchRequestService, never()).saveSearchRequest(any(SearchRequest.class));
        verifyLoggerMessages("Search request is empty");
    }

    @Sql(value = {"classpath:sql/createApplicationUsers.sql", "classpath:sql/createUserRequests.sql"}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:sql/clearDb.sql", executionPhase = AFTER_TEST_METHOD)
    @Test
    void removeRequestTest() throws Exception {
        mockMvc.perform(get("/request/remove/1")
                .session(getUserSession(mockMvc, TEST_USER_EMAIL, TEST_USER_PASS)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/request"))
                .andExpect(view().name(REDIRECT + REQUEST_VIEW));

        verify(searchRequestService).deleteSearchRequest(1L);
    }

    private void setupLogger(Class<?> clazz, Level level) {
        when(mockAppender.getName()).thenReturn("MockAppender");
        when(mockAppender.isStarted()).thenReturn(true);
        when(mockAppender.isStopped()).thenReturn(false);

        logger = (Logger) LogManager.getLogger(clazz);
        logger.addAppender(mockAppender);
        logger.setLevel(level);
    }

    private void verifyLoggerMessages(String... messages) {
        verify(mockAppender, times(messages.length)).append(captorLoggingEvent.capture());

        int i = 0;
        for (LogEvent loggingEvent : captorLoggingEvent.getAllValues()) {
            assertEquals(messages[i++], loggingEvent.getMessage().getFormattedMessage());
        }
    }
}