package com.wojtasj.SkillNest.tokens;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String accesToken;
    private String refreshToken;
}
