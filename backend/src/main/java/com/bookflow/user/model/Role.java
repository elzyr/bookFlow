package com.bookflow.user.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @Column(unique = true, nullable = false, name = "role_name")
    private String rolename;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(String roleName) {
        this.rolename = roleName;
    }
}
