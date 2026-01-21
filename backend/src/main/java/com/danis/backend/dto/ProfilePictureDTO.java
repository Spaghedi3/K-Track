package com.danis.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating profile picture URL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePictureDTO {
    private String profilePictureUrl;
}