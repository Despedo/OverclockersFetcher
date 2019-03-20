package com.overclockers.fetcher.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sent_topic")
public class SentTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long sentTopicId;
    @OneToOne
    @JoinColumn(name = "application_user_id", referencedColumnName = "user_application_id", nullable = false)
    private ApplicationUser applicationUser;
    @OneToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "topic_id", nullable = false)
    private ForumTopic forumTopic;
    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;
}
