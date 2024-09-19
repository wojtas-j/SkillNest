package com.wojtasj.SkillNest.validation;

import com.wojtasj.SkillNest.users.DTO.UserRegistrationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TutorRegistrationValidator implements ConstraintValidator<ValidTutorRegistration, UserRegistrationDto> {
    @Override
    public boolean isValid(UserRegistrationDto value, ConstraintValidatorContext context) {
        if (value.isTutor()) {
            boolean isValid = value.getTutorDetails() != null &&
                    value.getTutorDetails().getDescription() != null &&
                    !value.getTutorDetails().getDescription().isEmpty();

            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Description is required for tutors")
                        .addPropertyNode("tutorDetails.description")
                        .addConstraintViolation();
            }

            return isValid;
        }
        return true;
    }
}
