package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.repository.SearchRequestRepository;
import com.overclockers.fetcher.service.SearchRequestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SearchRequestServiceImpl implements SearchRequestService {

    private SearchRequestRepository repository;

    @Override
    public List<SearchRequest> findSearchRequestByUserId(Long userId) {
        return repository.findSearchRequestByUserId(userId);
    }

    @Override
    public List<SearchRequest> findSearchRequestByEmail(String email) {
        return repository.findSearchRequestByUserEmail(email);
    }

    @Override
    public SearchRequest findSearchRequestById(Long requestId) {
        return repository.findSearchRequestById(requestId);
    }

    @Override
    public SearchRequest saveSearchRequest(SearchRequest request) {
        return repository.save(request);
    }

    @Override
    public void deleteSearchRequest(Long requestId) {
        repository.deleteById(requestId);
    }
}
