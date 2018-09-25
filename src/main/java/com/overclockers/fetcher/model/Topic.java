package com.overclockers.fetcher.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Topic {
    private Long topicId;
    private User topicStarter;
    private String city;
    private String title;
    private String topicLink;
    private LocalDateTime topicDateTime;
    private LocalDateTime createdDateTime;
}
