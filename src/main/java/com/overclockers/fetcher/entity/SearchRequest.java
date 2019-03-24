package com.overclockers.fetcher.entity;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "request")
public class SearchRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "application_user_id")
    @ManyToOne(targetEntity = ApplicationUser.class)
    private ApplicationUser user;
    @Column(name = "request", nullable = false)
    private String request;
    @Column(name = "created_datetime", nullable = false)
    private ZonedDateTime createdDateTime;

    public String toString() {
        return this.getRequest();
    }
}
