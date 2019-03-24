package com.overclockers.fetcher.entity;

import lombok.*;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_forum")
public class ForumUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_nickname", nullable = false)
    private String nickname;
    @Column(name = "forum_id", nullable = false, unique = true)
    private Long userForumId;
    @Column(name = "created_datetime", nullable = false)
    private ZonedDateTime createdDateTime;
}
