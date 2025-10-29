package com.danis.ktrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User; // Import Spring Security User

import java.util.Optional;
@Configuration
public class SpringSecurityAuditorAware {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // This is a lambda implementation of the AuditorAware<String> interface.
        return () -> {
            // Get the current Authentication object from Spring Security
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null || "anonymousUser".equals(authentication.getPrincipal())) {
                // If no one is logged in or principal is anonymous, return "system" or empty
                // You might want to return null or a specific system user identifier
                return Optional.of("system");
            }

            // Get the username. This depends on your UserDetails implementation.
            // If using standard Spring Security User:
            Object principal = authentication.getPrincipal();
            String username;
            if (principal instanceof User) {
                username = ((User)principal).getUsername();
            } else if (principal instanceof String) {
                username = (String) principal;
            } else {
                // Handle other principal types or return a default/system user
                // For example, if you have a custom UserDetails object:
                // username = ((YourCustomUserDetails) principal).getUsername();
                username = "unknown"; // Fallback
            }

            return Optional.of(username);
        };
    }
}

