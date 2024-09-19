package com.wojtasj.SkillNest.users;

import com.wojtasj.SkillNest.users.DTO.UserLoginDto;
import com.wojtasj.SkillNest.users.DTO.UserRegistrationDto;
import com.wojtasj.SkillNest.users.Entities.User;

public interface UserService {
    User registerUser(UserRegistrationDto userDto);
    void updateLastLogin(String email);
    void deleteUser(Long userId);
    User updateEmail(Long userId, String newEmail);
    User updatePassword(Long userId, String newPassword);
    User findByEmail(String email);
}
