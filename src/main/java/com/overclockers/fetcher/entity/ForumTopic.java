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
@Table(name = "forumTopic")
public class ForumTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topicId")
    @EqualsAndHashCode.Exclude
    private Long topicId;
    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(targetEntity = ForumUser.class)
    private ForumUser user;
    @Column(name = "location")
    private String location;
    @Column(name = "title")
    private String title;
    @Column(name = "topicForumId")
    private Long topicForumId;
    @Column(name = "createdDateTime")
    private LocalDateTime createdDateTime;
    @Column(name = "updatedDateTime")
    private LocalDateTime updatedDateTime;
    @Column(name = "lastMessageDateTime")
    private LocalDateTime lastMessageDateTime;
    @Column(name = "sentToUser")
    @EqualsAndHashCode.Exclude
    private boolean sentToUser;
}
