package com.bookflow.user;

import com.bookflow.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "creationDate", target = "creationDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserDto toDto(User user);

    User toEntity(UserDto dto);

    @Named("rolesToStrings")
    default List<String> map(Set<Role> roles) {
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream().map(Role::getRoleName).collect(Collectors.toList());
    }
}
