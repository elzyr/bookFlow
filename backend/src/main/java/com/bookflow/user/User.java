package com.bookflow.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.Data;
import java.sql.Date;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="_user")
@Data
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true)
    private String username;

    @NonNull
    private String name;

    @NonNull
    private String password;

    @NonNull
    @Column(unique = true)
    private String email;

    private Date creationDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    private Set<Role> roles;
}
