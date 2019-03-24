package com.overclockers.fetcher.entity;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

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
    //ToDo implement storing updated_datetime
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private Long id;
    @JoinColumn(name = "user_forum_id")
    @ManyToOne(targetEntity = ForumUser.class)
    private ForumUser user;
    @Column(name = "location")
    private String location;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "forum_id", nullable = false, unique = true)
    private Long topicForumId;
    @Column(name = "topic_created_datetime", nullable = false)
    private ZonedDateTime topicCreatedDateTime;
    @Column(name = "topic_updated_datetime")
    private ZonedDateTime topicUpdatedDateTime;
    @Column(name = "created_datetime", nullable = false)
    private ZonedDateTime createdDateTime;
    @Column(name = "updated_datetime")
    private ZonedDateTime updatedDateTime;
    @OneToOne(mappedBy = "forumTopic")
    private SentTopic sentTopic;
}
