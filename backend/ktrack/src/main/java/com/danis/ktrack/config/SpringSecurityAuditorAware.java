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
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null || "anonymousUser".equals(authentication.getPrincipal())) {

                return Optional.of("system");
            }


            Object principal = authentication.getPrincipal();
            String username;
            if (principal instanceof User) {
                username = ((User)principal).getUsername();
            } else if (principal instanceof String) {
                username = (String) principal;
            } else {

                username = "unknown";
            }

            return Optional.of(username);
        };
    }
}

