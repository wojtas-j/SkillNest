package com.wojtasj.SkillNest.tokens;

import java.util.Optional;

import com.wojtasj.SkillNest.users.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
