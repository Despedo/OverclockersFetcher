package com.overclockers.fetcher.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDTO {
    @NotNull(message = "Please provide your first name")
    @Size(message = "Wrong first name", min = 1, max = 255)
    private String firstName;
    @NotNull(message = "Please provide your last name")
    @Size(message = "Wrong last name", min = 1, max = 255)
    private String lastName;
    @NotNull(message = "Please provide your email")
    @Email(message = "Wrong email")
    private String email;
}
