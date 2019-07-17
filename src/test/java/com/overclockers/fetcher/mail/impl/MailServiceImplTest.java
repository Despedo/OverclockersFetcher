package com.overclockers.fetcher.mail.impl;

import com.overclockers.fetcher.TestAppender;
import com.overclockers.fetcher.entity.ApplicationUser;
import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.ForumUser;
import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.mail.HtmlRender;
import com.overclockers.fetcher.mail.MailService;
import com.overclockers.fetcher.service.ForumTopicService;
import com.overclockers.fetcher.service.SearchRequestService;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.Session;
import java.time.ZonedDateTime;
import java.util.*;

import static com.overclockers.fetcher.TestAppender.createAppender;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class MailServiceImplTest {


    @Mock
    private HtmlRender render;
    @Mock
    private ForumTopicService topicService;
    @Mock
    private Mailer mailer;
    @Mock
    private SearchRequestService requestService;

    private MailService mailService;

    @BeforeEach
    void init() {
        mailService = new MailServiceImpl(render, topicService, mailer, requestService);
    }

    @Test
    void processUserRequestEmailWithoutRequestsTest() {
        TestAppender testAppender = createAppender(MailServiceImpl.class);
        ApplicationUser user = getDummyApplicationUser();
        List<SearchRequest> searchRequests = Collections.emptyList();
        when(requestService.findSearchRequestsByUserId(anyLong())).thenReturn(searchRequests);

        mailService.processUserRequestEmail(user);

        assertEquals(1, testAppender.getLogEvents().size());
        assertEquals(Level.INFO, testAppender.getLogEvents().get(0).getLevel());
        assertEquals("No topics were found by request for user '" + user.getEmail() + "'",
                testAppender.getLogEvents().get(0).getMessage().getFormattedMessage());
    }

    @Test
    void processUserRequestEmailWithRequestedTopicsTest() {
        TestAppender testAppender = createAppender(MailServiceImpl.class);
        String request = "1080Ti";
        ApplicationUser user = getDummyApplicationUser();
        List<SearchRequest> searchRequests = getDummySearchRequest(user, request);
        ForumUser forumUser = getDummyForumUser();
        List<ForumTopic> topics = getRequestDummyTopics(forumUser, request);
        Map<SearchRequest, List<ForumTopic>> topicsMap = Collections.singletonMap(SearchRequest.builder().request(request).build(), topics);

        when(requestService.findSearchRequestsByUserId(anyLong())).thenReturn(searchRequests);
        when(topicService.findTopicsMapForSending(searchRequests, user.getId())).thenReturn(topicsMap);
        when(render.renderHtmlTextForSearchRequestEmail(topicsMap)).thenReturn("<html>Some rendered Html</html>");
        ReflectionTestUtils.setField(mailService, "senderAddress", "test@mail.com");

        mailService.processUserRequestEmail(user);

        verify(requestService).findSearchRequestsByUserId(user.getId());
        verify(topicService).findTopicsMapForSending(searchRequests, user.getId());
        verify(render).renderHtmlTextForSearchRequestEmail(topicsMap);
        verify(mailer).sendMail(any(Email.class));
        verify(topicService).registerSentTopics(topics, user);
        assertEquals(2, testAppender.getLogEvents().size());
        assertEquals(Level.INFO, testAppender.getLogEvents().get(0).getLevel());
        assertEquals("Found '2' topics by requests '[" + request + "]' for user '" + user.getEmail() + "'.",
                testAppender.getLogEvents().get(0).getMessage().getFormattedMessage());
        assertEquals(Level.INFO, testAppender.getLogEvents().get(1).getLevel());
        assertEquals("Sending email to '" + user.getEmail() + "'.",
                testAppender.getLogEvents().get(1).getMessage().getFormattedMessage());

    }

    @Test
    void processUserRequestEmailWithoutRequestedTopicsTest() {
        String request = "1080Ti";
        ApplicationUser user = getDummyApplicationUser();
        List<SearchRequest> searchRequests = getDummySearchRequest(user, request);
        Map<SearchRequest, List<ForumTopic>> topicsMap = Collections.emptyMap();

        when(requestService.findSearchRequestsByUserId(anyLong())).thenReturn(searchRequests);
        when(topicService.findTopicsMapForSending(searchRequests, user.getId())).thenReturn(topicsMap);

        mailService.processUserRequestEmail(user);

        verify(requestService).findSearchRequestsByUserId(user.getId());
        verify(topicService).findTopicsMapForSending(searchRequests, user.getId());
        verify(render, never()).renderHtmlTextForSearchRequestEmail(anyMap());
        verify(mailer, never()).sendMail(any(Email.class));
        verify(topicService, never()).registerSentTopics(anyList(), any(ApplicationUser.class));
    }

    @Test
    void processRegistrationEmailTest() {
        TestAppender testAppender = createAppender(MailServiceImpl.class);
        ApplicationUser user = getDummyApplicationUser();
        String appUrl = "localhost";
        String href = appUrl + "/confirm?token=" + user.getConfirmationToken();
        when(render.renderHtmlTextForRegistrationConfirmation(href)).thenReturn("<html>Some rendered Html</html>");

        mailService.processRegistrationEmail(user, appUrl);

        verify(render).renderHtmlTextForRegistrationConfirmation(href);
        verify(mailer).sendMail(any(Email.class));
        assertEquals(1, testAppender.getLogEvents().size());
        assertEquals(Level.INFO, testAppender.getLogEvents().get(0).getLevel());
        assertEquals("Sending registration email to '" + user.getEmail() + "'",
                testAppender.getLogEvents().get(0).getMessage().getFormattedMessage());
    }

    private List<SearchRequest> getDummySearchRequest(ApplicationUser user, String request) {
        SearchRequest searchRequest = SearchRequest.builder()
                .id(44L)
                .user(user)
                .request(request)
                .createdDateTime(ZonedDateTime.parse("2018-03-01T08:15:30+01:00[Europe/Helsinki]"))
                .build();

        return Collections.singletonList(searchRequest);
    }

    private List<ForumTopic> getRequestDummyTopics(ForumUser forumUser, String request) {
        ForumTopic firstForumTopic = ForumTopic.builder()
                .id(45L)
                .topicForumId(435L)
                .location("Kyiv")
                .title("Just today " + request + " for low prices")
                .topicCreatedDateTime(ZonedDateTime.parse("2018-03-01T12:05:30+01:00[Europe/Helsinki]"))
                .createdDateTime(ZonedDateTime.parse("2018-03-01T12:10:30+01:00[Europe/Helsinki]"))
                .user(forumUser)
                .build();
        ForumTopic secondForumTopic = ForumTopic.builder()
                .id(47L)
                .topicForumId(456L)
                .location("Lviv")
                .title("New " + request + " from USA")
                .topicCreatedDateTime(ZonedDateTime.parse("2018-03-01T12:00:30+01:00[Europe/Helsinki]"))
                .createdDateTime(ZonedDateTime.parse("2018-03-01T12:10:35+01:00[Europe/Helsinki]"))
                .user(forumUser)
                .build();

        return Arrays.asList(firstForumTopic, secondForumTopic);
    }

    private ForumUser getDummyForumUser() {
        return ForumUser.builder()
                .id(32L)
                .userForumId(324L)
                .nickname("BestSeller")
                .createdDateTime(ZonedDateTime.parse("2018-02-01T10:34:30+01:00[Europe/Helsinki]"))
                .build();
    }

    private ApplicationUser getDummyApplicationUser() {
        return ApplicationUser.builder()
                .id(1L)
                .email("test@mail.com")
                .firstName("Alex")
                .lastName("Wink")
                .confirmationToken("7cf9437b-15c3-4705-8923-6a68a3894032")
                .password("$2a$10$loNMbKIal9dUik059QzPMOxel4IB.9yKeBMVHbLleOcVwHdKtfQse")
                .createdDateTime(ZonedDateTime.parse("2018-01-03T10:15:30+01:00[Europe/Helsinki]"))
                .activatedDateTime(ZonedDateTime.parse("2018-01-03T10:34:30+01:00[Europe/Helsinki]"))
                .enabled(true)
                .build();
    }

}