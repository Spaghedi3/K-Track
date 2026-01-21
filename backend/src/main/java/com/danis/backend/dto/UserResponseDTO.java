package com.danis.backend.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
}
