package com.bookflow.user;

import com.bookflow.exception.InvalidOldPasswordException;
import com.bookflow.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Nie Znaleziono uzytkownika"));
        return userMapper.toDto(user);
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

    public UserDto createUser(CreateUserRequestDto newUser) {
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nazwa użytkownika jest już zajęta");
        }
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Email jest już zajęty");
        }
        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new NotFoundException("Domyślna rola USER nie została znaleziona"));

        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setName(newUser.getName());
        user.setRoles(Set.of(userRole));
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setEmail(newUser.getEmail());
        user.setCreationDate(LocalDate.now());
        return (userMapper.toDto(userRepository.save(user)));
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Nie znaleziono użytkownika"));
        userRepository.delete(user);
    }


}
