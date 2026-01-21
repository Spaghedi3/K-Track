package com.danis.backend.dto;

import lombok.Data;

@Data
public class UserSignupDTO {
    private String fullName;
    private String email;
    private String password;
}
