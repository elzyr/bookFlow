package com.bookflow.user;

import java.time.LocalDate;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-13T20:33:26+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        LocalDate creationDate = null;
        List<String> roles = null;
        long id = 0L;
        String username = null;
        String email = null;
        String name = null;
        boolean active = false;

        creationDate = user.getCreationDate();
        roles = map( user.getRoles() );
        if ( user.getId() != null ) {
            id = user.getId();
        }
        username = user.getUsername();
        email = user.getEmail();
        name = user.getName();
        active = user.isActive();

        UserDto userDto = new UserDto( id, username, email, name, creationDate, roles, active );

        return userDto;
    }

    @Override
    public User toEntity(UserDto dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setRoles( stringsToRoles( dto.getRoles() ) );
        user.setId( dto.getId() );
        user.setUsername( dto.getUsername() );
        user.setName( dto.getName() );
        user.setEmail( dto.getEmail() );
        user.setCreationDate( dto.getCreationDate() );
        user.setActive( dto.isActive() );

        return user;
    }
}
