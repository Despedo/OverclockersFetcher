package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.SearchRequest;

import java.util.List;

public interface SearchRequestService {
    List<SearchRequest> findSearchRequestByUserId(Long userId);

    SearchRequest findSearchRequestById(Long requestId);

    SearchRequest saveSearchRequest(SearchRequest request);

    void deleteSearchRequest(Long requestId);
}
