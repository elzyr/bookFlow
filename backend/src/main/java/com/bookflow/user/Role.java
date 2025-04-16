package com.bookflow.user;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(String roleName) {
        this.roleName = roleName;
    }
}
