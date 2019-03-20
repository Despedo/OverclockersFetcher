package com.overclockers.fetcher.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class SearchRequestDTO {
    private Long requestId;
    private String request;
    private String createdDateTime;
}
