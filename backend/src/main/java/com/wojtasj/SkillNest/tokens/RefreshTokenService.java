package com.wojtasj.SkillNest.tokens;

import com.wojtasj.SkillNest.users.Entities.User;
import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    boolean validateRefreshToken(String token);
    void deleteRefreshToken(String token);
    void deleteByUser(User user);
    Optional<RefreshToken> findByToken(String token);
}
