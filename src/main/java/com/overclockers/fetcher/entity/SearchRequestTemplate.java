package com.overclockers.fetcher.entity;

import lombok.*;

@Getter
@Setter
@Builder
public class SearchRequestTemplate {
    private Long requestId;
    private String request;
    private String createdDateTime;
}
