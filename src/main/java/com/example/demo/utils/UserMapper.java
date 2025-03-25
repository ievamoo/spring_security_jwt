package com.example.demo.utils;

import com.example.demo.dto.UserDto;
import com.example.demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto modelToDto(User user);

    User dtoToModel(UserDto userDto);

    void updateUserFromDto(UserDto dto, @MappingTarget User entity);

}
