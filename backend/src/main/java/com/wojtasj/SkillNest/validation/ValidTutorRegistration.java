package com.wojtasj.SkillNest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TutorRegistrationValidator.class)
public @interface ValidTutorRegistration {
    String message() default "Tutor details are required when registering as a tutor";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
