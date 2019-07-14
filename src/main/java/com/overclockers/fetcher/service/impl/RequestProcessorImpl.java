package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.SearchRequest;
import com.overclockers.fetcher.service.RequestProcessor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

@Component
public class RequestProcessorImpl implements RequestProcessor {

    private static final String SPACE_CHAR = " ";
    private static final String EMPTY_CHAR = "";
    // This RexExp split string by words, numbers, spaces and '+' '-' characters
    private static final String SPLIT_REGEX = "[-+]|\\s|(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)";
    private static final String SPACE_SEQUENCE_REGEX = "\\s+";
    private static final String SPACE_REGEX = "\\s";

    @Override
    public Map<String, Set<String>> generateRequestsPermutationsMap(List<SearchRequest> searchRequests) {
        return searchRequests.parallelStream()
                .collect(groupingBy(SearchRequest::getRequest, mapping(searchRequest -> generatePermutations(searchRequest.getRequest()),
                        Collector.of(HashSet::new, Set::addAll, (x, y) -> {
                            x.addAll(y);
                            return x;
                        }))));
    }

    private Set<String> generatePermutations(String request) {
        Set<String> permutations = new HashSet<>();
        String preparedRequest = request.replaceAll(SPLIT_REGEX, SPACE_CHAR).replaceAll(SPACE_SEQUENCE_REGEX, SPACE_CHAR);
        String[] split = preparedRequest.split(SPACE_REGEX);

        permute(permutations, preparedRequest, 0, split.length - 1);
        // ToDo fix this by SQL query
        permutations.add(request.replaceAll(SPACE_CHAR, EMPTY_CHAR));
        return permutations;
    }

    private void permute(Set<String> permutations, String s, int l, int r) {
        if (l == r) {
            permutations.add(s.replaceAll(SPACE_SEQUENCE_REGEX, SPACE_CHAR).trim());
        } else {
            for (int i = l; i <= r; i++) {
                s = swapWords(s, l, i);
                permute(permutations, s, l + 1, r);
            }
        }
    }

    private String swapWords(String s, int i, int j) {
        String temp;
        String[] split = s.split(SPACE_REGEX);
        temp = split[i];
        split[i] = split[j];
        split[j] = temp;
        return String.join(SPACE_CHAR, split);
    }

}
