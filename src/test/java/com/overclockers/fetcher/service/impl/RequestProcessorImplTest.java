package com.overclockers.fetcher.service.impl;

import com.google.common.collect.Sets;
import com.overclockers.fetcher.entity.SearchRequest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestProcessorImplTest {

    private RequestProcessorImpl requestProcessor = new RequestProcessorImpl();

    @Test
    void generateRequestsPermutationsTest() {
        SearchRequest firstRequest = SearchRequest.builder().request("1080ti titan").build();
        SearchRequest secondRequest = SearchRequest.builder().request("i8700k").build();
        Set<String> firstRequestPermutations = Sets.newHashSet(
                "1080 ti titan",
                "1080 titan ti",
                "ti 1080 titan",
                "ti titan 1080",
                "titan 1080 ti",
                "titan ti 1080"
        );
        Set<String> secondRequestPermutations = Sets.newHashSet(
                "i 8700 k",
                "i k 8700",
                "8700 i k",
                "8700 k i",
                "k i 8700",
                "k 8700 i"
        );
        Map<String, Set<String>> expectedRequestsPermutationsMap = new HashMap<>();
        expectedRequestsPermutationsMap.put(firstRequest.getRequest(), firstRequestPermutations);
        expectedRequestsPermutationsMap.put(secondRequest.getRequest(), secondRequestPermutations);


        Map<String, Set<String>> requestsPermutationsMap = requestProcessor.generateRequestsPermutationsMap(Arrays.asList(firstRequest, secondRequest));

        assertEquals(expectedRequestsPermutationsMap, requestsPermutationsMap);
    }
}