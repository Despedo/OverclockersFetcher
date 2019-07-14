package com.overclockers.fetcher.utils;

import com.overclockers.fetcher.dto.SearchRequestDTO;
import com.overclockers.fetcher.entity.SearchRequest;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchRequestConverterTest {

    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";

    SearchRequestConverter converter = new SearchRequestConverter();

    @Test
    void convertSearchRequests() {
        Long id = 1L;
        String request = "1080";
        ZonedDateTime dateTime = ZonedDateTime.now();
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));


        List<SearchRequest> searchRequests = new ArrayList<>(Collections.singleton(
                SearchRequest.builder()
                        .id(id)
                        .request(request)
                        .createdDateTime(dateTime)
                        .build()));

        List<SearchRequestDTO> dtos = converter.convertSearchRequests(searchRequests);

        assertEquals(id, dtos.get(0).getRequestId());
        assertEquals(request, dtos.get(0).getRequest());
        assertEquals(formattedDateTime, dtos.get(0).getCreatedDateTime());
    }
}