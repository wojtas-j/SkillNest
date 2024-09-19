package com.wojtasj.SkillNest.users.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TutorDetailsDto {
    @NotBlank(message = "Description is mandate")
    private String description;
}