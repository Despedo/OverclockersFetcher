package com.overclockers.fetcher.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "searchRequest")
public class SearchRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requestId")
    private Long requestId;
    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(targetEntity = ApplicationUser.class)
    private ApplicationUser user;
    @Column(name = "request")
    private String request;
    @Column(name = "createdDateTime")
    private LocalDateTime createdDateTime;
}
