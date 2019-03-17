package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.SearchRequest;

import java.util.List;

public interface SearchRequestService {
    List<SearchRequest> findSearchRequestsByUserId(Long userId);

    List<SearchRequest> findSearchRequestsByUserName(String userName);

    SearchRequest findSearchRequestById(Long requestId);

    SearchRequest saveSearchRequest(SearchRequest request);

    void deleteSearchRequest(Long requestId);
}
