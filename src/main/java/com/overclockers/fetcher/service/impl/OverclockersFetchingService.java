package com.overclockers.fetcher.service.impl;

import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.parser.OverclockersParser;
import com.overclockers.fetcher.service.FetchingService;
import com.overclockers.fetcher.service.ForumTopicService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.overclockers.fetcher.constants.OverclockersConstants.FIRST_PAGE_SELLING_PATH;
import static com.overclockers.fetcher.constants.OverclockersConstants.HOST_URL;

@Log4j2
@Service
@RequiredArgsConstructor
public class OverclockersFetchingService implements FetchingService {

    @NonNull
    private OverclockersParser parser;
    @NonNull
    private ForumTopicService topicService;

    private boolean isColdStart = true;

    @Override
    public void fetchAndSaveTopics() {
        String firstPageUrl = HOST_URL + FIRST_PAGE_SELLING_PATH;

        if (isColdStart) {
            fetchingColdStart(firstPageUrl);
        } else {
            fetchingHotStart(firstPageUrl);
        }
    }

    private void fetchingHotStart(String firstPageUrl) {
        log.info("Fetching 'hot start'");
        savePage(firstPageUrl);
        log.info("Fetching 'hot start' finished");
    }

    private void fetchingColdStart(String firstPageUrl) {
        log.info("Fetching 'cold start'");
        int coldStartFetchingSize = 10;
        int nextPage = 0;
        for (int i = 0; i < coldStartFetchingSize; i++) {
            if (i == 0) {
                savePage(firstPageUrl);
            } else {
                String nextPageUrl = firstPageUrl + "&start=" + nextPage;
                int nextPageShift = 40;
                nextPage += nextPageShift;
                savePage(nextPageUrl);
            }
        }
        isColdStart = false;
        log.info("Fetching 'cold start finished'");
    }

    private void savePage(String url) {
        //ToDO fetch topics by id on cold start
        List<ForumTopic> forumTopics = parser.getForumTopics(url);
        for (ForumTopic topic : forumTopics) {
            topicService.saveTopic(topic);
        }
    }
}
