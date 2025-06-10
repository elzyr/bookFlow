package com.bookflow.auth;

import com.bookflow.config.JwtService;
import com.bookflow.user.User;
import com.bookflow.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Nieprawidłowy login lub hasło");
        }

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        if(!user.isActive()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Konto użytkownika nieaktywne");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(60 * 15) // 15 min
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .sameSite("None")
                .maxAge(7 * 24 * 60 * 60) // 7 dni
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> c.getName().equals("refresh"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshToken == null ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String newAccessToken = jwtService.generateToken(user);

        ResponseCookie newJwtCookie = ResponseCookie.from("jwt", newAccessToken)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .sameSite("None")
                .maxAge(60 * 15)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, newJwtCookie.toString());
        return ResponseEntity.ok().build();
    }


}
