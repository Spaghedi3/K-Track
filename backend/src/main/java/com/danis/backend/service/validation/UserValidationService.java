package com.danis.backend.service.validation;

import com.danis.backend.domain.model.entities.User;

public interface UserValidationService {
    void validateUser(User user);
    void validateProfile(User user);
}
