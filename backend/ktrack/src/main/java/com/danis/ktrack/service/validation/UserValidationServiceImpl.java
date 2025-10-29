package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.User;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserValidationServiceImpl implements UserValidationService{

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public void validate(User user) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if(user==null){
            errors.add("User cannot be null");
            throw new ValidationException(errors);
        }

        if(user.getUsername()==null || user.getUsername().isBlank()){
            errors.add("Username cannot be empty");
        }

        if(user.getEmail()==null || user.getEmail().isBlank()){
            errors.add("Email cannot be empty");
        }
        else if(!EMAIL_PATTERN.matcher(user.getEmail()).matches()){
            errors.add("Email format is not valid");
        }

        if(!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
