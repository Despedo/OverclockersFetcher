package com.overclockers.fetcher.utils;

import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.dto.SearchRequestDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SearchRequestConverter {

    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";

    public List<SearchRequestDTO> convertSearchRequests(List<SearchRequest> searchRequests) {
        List<SearchRequestDTO> templates = new ArrayList<>(searchRequests.size());
        searchRequests.sort(Comparator.comparing(SearchRequest :: getCreatedDateTime).reversed());
        searchRequests.forEach(searchRequest -> templates.add(convert(searchRequest)));
        return templates;
    }

    private SearchRequestDTO convert(SearchRequest searchRequest) {
        String createdDateTime = searchRequest.getCreatedDateTime().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        return SearchRequestDTO.builder().
                requestId(searchRequest.getId()).
                request(searchRequest.getRequest()).
                createdDateTime(createdDateTime).
                build();
    }
}
