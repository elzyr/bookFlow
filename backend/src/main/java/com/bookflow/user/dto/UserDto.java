package com.bookflow.user.dto;

import com.bookflow.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private String name;
    private String creationDate;
    private List<String> roles;
}
