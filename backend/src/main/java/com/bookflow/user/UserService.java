package com.bookflow.user;

import com.bookflow.exception.InvalidOldPasswordException;
import com.bookflow.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
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

    public UserDto create(UserDto userDto) {

    }
}
