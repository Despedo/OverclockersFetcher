package com.overclockers.fetcher.mail;

import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;

import java.util.List;
import java.util.Map;

public interface HtmlRender {
    String renderHtmlTextForSearchRequestEmail(Map<SearchRequest, List<ForumTopic>> topicsMap);

    String renderHtmlTextForRegistrationConfirmation(String href);
}
