package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.repository.SearchRequestRepository;
import com.overclockers.fetcher.service.SearchRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({SpringExtension.class})
class SearchRequestServiceImplTest {

    @Mock
    private SearchRequestRepository repository;

    private SearchRequestService searchRequestService;

    @BeforeEach
    void init() {
        searchRequestService = new SearchRequestServiceImpl(repository);
    }


    @Test
    void findSearchRequestsByUserIdTest() {
        Long userId = 145L;

        searchRequestService.findSearchRequestsByUserId(userId);

        verify(repository, times(1)).findSearchRequestByUserId(userId);
    }

    @Test
    void findSearchRequestsByUserNameTest() {
        String email = "test@mail.com";

        searchRequestService.findSearchRequestsByUserName(email);

        verify(repository, times(1)).findSearchRequestByUserEmail(email);
    }

    @Test
    void findSearchRequestByIdTest() {
        Long requestId = 34L;

        searchRequestService.findSearchRequestById(requestId);

        verify(repository, times(1)).findSearchRequestById(requestId);
    }

    @Test
    void saveSearchRequestTest() {
        SearchRequest request = SearchRequest.builder()
                .request("1080")
                .build();

        searchRequestService.saveSearchRequest(request);

        verify(repository, times(1)).save(request);
    }

    @Test
    void deleteSearchRequestTest() {
        Long requestId = 34L;

        searchRequestService.deleteSearchRequest(requestId);

        verify(repository, times(1)).deleteById(requestId);
    }
}