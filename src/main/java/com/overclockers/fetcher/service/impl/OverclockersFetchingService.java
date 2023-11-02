package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.parser.OverclockersParser;
import com.overclockers.fetcher.service.FetchingService;
import com.overclockers.fetcher.service.ForumTopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import static com.overclockers.fetcher.constants.OverclockersConstants.FIRST_PAGE_SELLING_PATH;
import static com.overclockers.fetcher.constants.OverclockersConstants.HOST_URL;

@Log4j2
@Service
@RequiredArgsConstructor
public class OverclockersFetchingService implements FetchingService {

    private final OverclockersParser parser;
    private final ForumTopicService topicService;

    private boolean isColdStart = true;

    @Override
    public void fetchAndSaveTopics() {
        String firstPageUrl = HOST_URL + FIRST_PAGE_SELLING_PATH;
        if (isColdStart) {
            log.info("Fetching 'cold start'");
            fetchingColdStart(firstPageUrl);
            log.info("Fetching 'cold start' finished");
        } else {
            log.info("Fetching 'hot start'");
            fetchingHotStart(firstPageUrl);
            log.info("Fetching 'hot start' finished");
        }
    }

    private void fetchingHotStart(String firstPageUrl) {
        topicService.saveTopics(parser.getForumTopics(firstPageUrl));
    }

    private void fetchingColdStart(String firstPageUrl) {
        int coldStartFetchingSize = 10;
        int nextPageStart = 40;
        for (int i = 0; i < coldStartFetchingSize; i++) {
            if (i == 0) {
                topicService.saveTopics(parser.getForumTopics(firstPageUrl));
            } else {
                String nextPageUrl = firstPageUrl + "&start=" + nextPageStart;
                int nextPageShift = 40;
                nextPageStart += nextPageShift;
                topicService.saveTopics(parser.getForumTopics(nextPageUrl));
            }
        }
        isColdStart = false;
    }

}
