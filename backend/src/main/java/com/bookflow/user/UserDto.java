package com.bookflow.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class UserDto {
    private long id;
    private String username;
    private String email;
    private String name;
    private String creationDate;
    private List<String> roles;
    private boolean isActive;
}
