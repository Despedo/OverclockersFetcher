package com.overclockers.fetcher.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_application")
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Please provide a valid e-mail")
    @NotEmpty(message = "Please provide an e-mail")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "first_name", nullable = false)
    @NotEmpty(message = "Please provide your first name")
    private String firstName;
    @Column(name = "last_name", nullable = false)
    @NotEmpty(message = "Please provide your last name")
    private String lastName;
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
    @Column(name = "confirmation_token", nullable = false, unique = true)
    private String confirmationToken;
    @Column(name = "activated_datetime")
    private ZonedDateTime activatedDateTime;
    @Column(name = "created_datetime", nullable = false)
    private ZonedDateTime createdDateTime;
}
