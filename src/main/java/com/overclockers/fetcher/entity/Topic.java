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
@Table(name = "topics")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topicId")
    private Long topicId;
    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(targetEntity = User.class)
    private User user;
    @Column(name = "location")
    private String location;
    @Column(name = "title")
    private String title;
    @Column(name = "topicForumId")
    private Long topicForumId;
    @Column(name = "createdDateTime")
    private LocalDateTime createdDateTime;
    @Column(name = "sentToUser")
    private boolean sentToUser;
}
