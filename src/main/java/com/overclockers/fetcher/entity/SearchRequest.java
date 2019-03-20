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
@Table(name = "request")
public class SearchRequest {
    //ToDo change id naming, check other fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;
    @JoinColumn(name = "application_user_id")
    @ManyToOne(targetEntity = ApplicationUser.class)
    private ApplicationUser user;
    @Column(name = "request", nullable = false)
    private String request;
    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDateTime;
}
