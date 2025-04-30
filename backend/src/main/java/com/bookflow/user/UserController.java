package com.bookflow.user;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElse(null);

        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        return ResponseEntity.ok(
                new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getName(),
                        user.getCreationDate().toString(),
                        roles
                ));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/passwordChange")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        Optional<User> optionalUser = userRepository.findById(request.getUserId());

       if(optionalUser.isEmpty()) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
       }
       User user = optionalUser.get();

        PasswordCheck passwordCheck = new PasswordCheck(request.getOldPassword(), request.getNewPassword());

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Stare hasło jest nieprawidłowe");
        }


        if (!passwordCheck.isNewPasswordValid()) {
            return ResponseEntity.badRequest().body("Nowe hasło nie spełnia wymagań (min. 8 znaków, duża litera, mała litera, cyfra)");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Hasło zostało zmienione");
    }


}
