package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.SearchRequest;

import java.util.List;

public interface SearchRequestService {
    List<SearchRequest> findSearchRequestByUserId(Long userId);

    List<SearchRequest> findSearchRequestByEmail(String userName);

    SearchRequest findSearchRequestById(Long requestId);

    SearchRequest saveSearchRequest(SearchRequest request);

    void deleteSearchRequest(Long requestId);
}
