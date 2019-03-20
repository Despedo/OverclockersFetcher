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
@Table(name = "topic")
public class ForumTopic {
    // ToDo change id naming, first message date time, delete sentToUser check other fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private Long topicId;
    @JoinColumn(name = "user_forum_id")
    @ManyToOne(targetEntity = ForumUser.class)
    private ForumUser user;
    @Column(name = "location")
    private String location;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "forum_id", nullable = false, unique = true)
    private Long topicForumId;
    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDateTime;
    @Column(name = "updated_datetime")
    private LocalDateTime updatedDateTime;
    @Column(name = "last_message_datetime", nullable = false)
    private LocalDateTime lastMessageDateTime;
    @Column(name = "sent_to_user")
    @EqualsAndHashCode.Exclude
    private boolean sentToUser;
    @OneToOne(mappedBy = "forumTopic")
    private SentTopic sentTopic;
}
