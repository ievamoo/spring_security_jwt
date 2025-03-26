package com.example.demo.utils;

import com.example.demo.dto.UserDto;
import com.example.demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto modelToDto(User user);

    /**
     * Updates only the profile information (firstName, lastName, email) of a user.
     * This method preserves all other fields including id, username, password, and roles.
     *
     * @param dto DTO containing the updated user information
     * @param entity The user entity to update
     */
    default void updateUserProfile(UserDto dto, @MappingTarget User entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
    }
}
