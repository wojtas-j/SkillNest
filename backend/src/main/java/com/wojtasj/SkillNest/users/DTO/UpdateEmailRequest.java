package com.wojtasj.SkillNest.users.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateEmailRequest {
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String newEmail;
}
