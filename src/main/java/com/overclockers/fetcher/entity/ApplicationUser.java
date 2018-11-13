package com.overclockers.fetcher.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applicationUser")
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;
    @Column(name = "userName")
    private String userName;
    @Column(name = "email")
    private String email;
    @Column(name = "createdDateTime")
    private LocalDateTime createdDateTime;
}
