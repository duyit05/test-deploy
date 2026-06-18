package com.example.optimal.mapper;


import com.example.optimal.dto.UserResponse;
import com.example.optimal.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse (User user);
}
