package com.overclockers.fetcher.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;
    @Column(name = "userName")
    private String username;
    @Column(name = "profileLink")
    private String profileLink;
    @Column(name = "createdDateTime")
    private LocalDateTime createdDateTime;
    @JoinColumn(name = "topicId")
    @OneToMany(targetEntity = Topic.class)
    private List<Topic> topics;
}
