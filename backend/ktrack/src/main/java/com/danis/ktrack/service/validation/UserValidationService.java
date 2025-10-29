package com.danis.ktrack.service.validation;


import com.danis.ktrack.domain.model.entities.User;
import jakarta.validation.ValidationException;

public interface UserValidationService {

void validate(User user) throws ValidationException;
}
