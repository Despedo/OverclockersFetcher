package com.overclockers.fetcher.service;

import com.overclockers.fetcher.entity.SearchRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RequestProcessor {

    Map<String, Set<String>> generateRequestsPermutationsMap(List<SearchRequest> searchRequests);

}
