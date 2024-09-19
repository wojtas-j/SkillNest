package com.wojtasj.SkillNest.users;

import com.wojtasj.SkillNest.exceptions.UserNotFoundException;
import com.wojtasj.SkillNest.tokens.RefreshTokenService;
import com.wojtasj.SkillNest.users.DTO.UserLoginDto;
import com.wojtasj.SkillNest.users.DTO.UserRegistrationDto;
import com.wojtasj.SkillNest.users.Entities.Role;
import com.wojtasj.SkillNest.users.Entities.TutorDetails;
import com.wojtasj.SkillNest.users.Entities.User;
import com.wojtasj.SkillNest.exceptions.EmailExistsException;
import com.wojtasj.SkillNest.users.Repositories.RoleRepository;
import com.wojtasj.SkillNest.users.Repositories.TutorDetailsRepository;
import com.wojtasj.SkillNest.users.Repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TutorDetailsRepository tutorDetailsRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(@Valid UserRegistrationDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailExistsException("Email is already in use");
        }

        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .enabled(true)
                .roles(Collections.singleton(getRole("USER")))
                .build();

        if (userDto.isTutor()) {
            if (userDto.getTutorDetails() == null || userDto.getTutorDetails().getDescription() == null) {
                throw new IllegalArgumentException("Description is required for tutors");
            }

            user.getRoles().add(getRole("TUTOR"));

            TutorDetails tutorDetails = TutorDetails.builder()
                    .user(user)
                    .description(userDto.getTutorDetails().getDescription())
                    .build();

            user.setTutorDetails(tutorDetails);
        }

        return userRepository.save(user);
    }

    @Override
    public void updateLastLogin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));

        refreshTokenService.deleteByUser(user);

        if (user.getTutorDetails() != null) {
            tutorDetailsRepository.delete(user.getTutorDetails());
        }

        userRepository.delete(user);
    }

    @Override
    public User updateEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));

        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email is already in use");
        }

        user.setEmail(newEmail);
        User updatedUser = userRepository.save(user);

        refreshTokenService.deleteByUser(user);

        return updatedUser;
    }

    @Override
    public User updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));

        user.setPassword(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(user);

        refreshTokenService.deleteByUser(user);

        return updatedUser;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Role getRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));
    }
}
