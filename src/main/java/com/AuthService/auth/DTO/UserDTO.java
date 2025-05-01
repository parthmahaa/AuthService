package com.AuthService.auth.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    private Long userId;

    private String name;
    private String email;

    private String username;

    private String password;
    private String roles; // Comma-separated roles
}

