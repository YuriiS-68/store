package ru.skypro.homework.service;

import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    UserDto addUser(CreateUserDto createUser);

    UserDto updateUser(UserDto userDto)throws NotFoundException;

    UserDto getUserById(Long id);

    Collection<UserDto> getAllUsers();
}
