package com.bookflow.data;

import com.bookflow.auth.SecurityConfig;
import com.bookflow.user.Role;
import com.bookflow.user.RoleRepository;
import com.bookflow.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.bookflow.user.User;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Component
public class UserDataGenerator implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        List<User> users = addUsers();
    }

    private Role addRole(String roleName) {
        Optional<Role> roles = roleRepository.findByRolename(roleName);
        if(roles.isPresent()) {
            return roles.get();
        }
        return roleRepository.save(new Role(roleName));
    }

    private List<User> addUsers() {
        Role userRole = addRole("USER");
        Role adminRole = addRole("ADMIN");
        Set<Role> userRoles = Set.of(userRole);
        Set<Role> adminRoles = Set.of(adminRole,userRole);

        return List.of(
                addUser("user1","user1@test.com","Anna Nowak",userRoles),
                addUser("employee1","employees1@example.com","Kamil Winczewski",adminRoles),
                addUser("user2","user2@test.com","Bartek Borowik",userRoles),
                addUser("employee2","employees2@example.com","Martyna Szymanska",adminRoles)
        );
    }


    private User addUser(String username, String email,String name, Set<Role> roles) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if(optionalUser.isEmpty()){
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setName(name);
            user.setRoles(new HashSet<>(roles));
            user.setPassword(passwordEncoder.encode(username));
            user.setCreationDate(new Date(System.currentTimeMillis()));
            return userRepository.save(user);
        }
        return optionalUser.get();
    }

}
