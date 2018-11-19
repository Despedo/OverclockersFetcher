package com.overclockers.fetcher.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @Column(name = "user_forum_id")
    private Long userId;
    @Column(name = "user_name", nullable = false)
    private String username;
    @Column(name = "forum_id", nullable = false, unique = true)
    private Long userForumId;
    @Column(name = "registered_datetime", nullable = false)
    private LocalDateTime registeredDateTime;
}
