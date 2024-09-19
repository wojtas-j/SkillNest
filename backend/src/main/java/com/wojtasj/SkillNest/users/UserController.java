package com.wojtasj.SkillNest.users;

import com.wojtasj.SkillNest.exceptions.TokenRefreshException;
import com.wojtasj.SkillNest.misc.ApiPaths;
import com.wojtasj.SkillNest.tokens.*;
import com.wojtasj.SkillNest.users.DTO.*;
import com.wojtasj.SkillNest.users.Entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.Users.BASE)
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping(ApiPaths.Users.REGISTER)
    @Operation(summary = "Register of new user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto userDto) {
        try {
            User user = userService.registerUser(userDto);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(ApiPaths.Users.LOGIN)
    @Operation(summary = "User log in")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );

            String accessToken = jwtTokenProvider.generateToken(loginDto.getEmail());
            User user = userService.findByEmail(loginDto.getEmail());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            userService.updateLastLogin(loginDto.getEmail());

            return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken.getToken()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @PostMapping(ApiPaths.Users.REFRESH_TOKEN)
    @Operation(summary = "User token refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        try {
            boolean isValid = refreshTokenService.validateRefreshToken(requestRefreshToken);

            if (!isValid) {
                return ResponseEntity.status(403).body("Invalid refresh token");
            }

            RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                    .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));

            String email = refreshToken.getUser().getEmail();

            String accessToken = jwtTokenProvider.generateToken(email);

            return ResponseEntity.ok(new JwtResponse(accessToken, requestRefreshToken));
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping(ApiPaths.Users.LOGOUT)
    @Operation(summary = "User logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        refreshTokenService.deleteRefreshToken(requestRefreshToken);

        return ResponseEntity.ok("User logged out successfully");
    }

    @Operation(summary = "User password update")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(ApiPaths.Users.UPDATE_PASSWORD)
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        Long userId = getCurrentUserId();

        try {
            User updatedUser = userService.updatePassword(userId, request.getNewPassword());
            return ResponseEntity.ok("Password updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping(ApiPaths.Users.DELETE)
    @Operation(summary = "Delete user account")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteUser() {
        Long userId = getCurrentUserId();
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Long getCurrentUserId() {
        String email = jwtTokenProvider.getEmailFromToken(getCurrentAccessToken());
        User user = userService.findByEmail(email);
        return user.getId();
    }

    private String getCurrentAccessToken() {
        String bearerToken = request.getHeader("Authorization");
        if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
