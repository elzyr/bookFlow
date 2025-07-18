package com.bookflow.user;

import com.bookflow.book.PageDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String username = auth.getName();
        return ResponseEntity.ok(userMapper.toDto(userService.findByUsername(username)));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "").httpOnly(true).path("/").maxAge(0).build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/passwordChange")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.getUserName(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageDto<UserDto>> getAllUsers(
            @PageableDefault(size = 5, sort = "username") Pageable pageable
    ) {
        Page<UserDto> page = userService.findAllPageable(pageable).map(userMapper::toDto);

        PageDto<UserDto> dto = new PageDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{username}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeAccountStatus(@PathVariable String username, @RequestParam boolean status) {
        userService.changeStatus(username, status);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequestDto user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(userService.createUser(user)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

}
