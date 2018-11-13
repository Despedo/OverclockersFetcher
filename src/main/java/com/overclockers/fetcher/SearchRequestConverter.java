package com.overclockers.fetcher;

import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.entity.SearchRequestTemplate;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SearchRequestConverter {

    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";

    public List<SearchRequestTemplate> convertSearchRequests(List<SearchRequest> searchRequests) {
        List<SearchRequestTemplate> templates = new ArrayList<>(searchRequests.size());
        searchRequests.sort(Comparator.comparing(SearchRequest :: getCreatedDateTime).reversed());
        searchRequests.forEach(searchRequest -> templates.add(convert(searchRequest)));
        return templates;
    }

    private SearchRequestTemplate convert(SearchRequest searchRequest) {
        String createdDateTime = searchRequest.getCreatedDateTime().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        return SearchRequestTemplate.builder().
                requestId(searchRequest.getRequestId()).
                request(searchRequest.getRequest()).
                createdDateTime(createdDateTime).
                build();
    }
}
