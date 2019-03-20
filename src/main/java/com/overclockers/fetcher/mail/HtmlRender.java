package com.overclockers.fetcher.mail;

import com.overclockers.fetcher.entity.ForumTopic;
import com.overclockers.fetcher.entity.SearchRequest;

import java.util.List;

public interface HtmlRender {
    String renderHtmlTextForSearchRequestEmail(List<SearchRequest> searchRequests, List<ForumTopic> topics);

    String renderHtmlTextForRegistrationConfirmation(String href);
}
