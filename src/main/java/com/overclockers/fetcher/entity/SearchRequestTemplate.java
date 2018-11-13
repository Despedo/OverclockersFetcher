package com.overclockers.fetcher.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SearchRequestTemplate {
    private Long requestId;
    private String request;
    private String createdDateTime;
}
