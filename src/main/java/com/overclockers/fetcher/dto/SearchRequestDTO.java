package com.overclockers.fetcher.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchRequestDTO {
    private Long requestId;
    private String request;
    private String createdDateTime;
}
