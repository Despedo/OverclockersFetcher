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
    @JoinColumn(name = "topicStarterId", nullable = false)
    @ManyToOne(targetEntity = User.class)
    private User topicStarter;
    @Column(name = "city")
    private String city;
    @Column(name = "title")
    private String title;
    @Column(name = "topicLink")
    private String topicLink;
    @Column(name = "createdDateTime")
    private LocalDateTime createdDateTime;
    @Column(name = "lastMessageDateTime")
    private LocalDateTime lastMessageDateTime;
}
