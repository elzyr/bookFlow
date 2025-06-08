package com.bookflow.user;

import com.bookflow.exception.InvalidOldPasswordException;
import com.bookflow.exception.NotFoundException;
import com.bookflow.exception.UserRegisterException;
import com.bookflow.role.Role;
import com.bookflow.role.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Nie Znaleziono uzytkownika"));
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Nie Znaleziono uzytkownika"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidOldPasswordException("Stare hasło jest niepoprawne");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void changeStatus(String userName, boolean status) {
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new NotFoundException("Nie Znaleziono uzytkownika"));
        user.setActive(status);
        userRepository.save(user);
    }

    public User createUser(CreateUserRequestDto newUser) {
        String normalizedUsername = newUser.getUsername().trim().toLowerCase();
        String normalizedEmail = newUser.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new UserRegisterException(
                    "Nazwa użytkownika jest już zajęta");
        }

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserRegisterException(
                    "Email jest już zajęty");
        }

        String rawPassword = newUser.getPassword();
        if (rawPassword == null ||
                !rawPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$")) {
            throw new UserRegisterException(
                    "Hasło musi zawierać co najmniej: jedną małą literę, " +
                            "jedną dużą literę, jedną cyfrę oraz jeden znak specjalny");
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new NotFoundException("Domyślna rola USER nie została znaleziona"));

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setName(newUser.getName());
        user.setRoles(Set.of(userRole));
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(normalizedEmail);
        user.setCreationDate(LocalDate.now());

        return userRepository.save(user);
    }


    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono użytkownika"));
        userRepository.delete(user);
    }


}
