package ru.skypro.homework.service;

import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.exception.NotFoundException;

import java.util.Collection;

public interface UserService {
    UserDto addUser(CreateUserDto createUser);

    UserDto updateUser(String userName, UserDto userDto)throws NotFoundException;

    UserDto getUserById(Long id);

    Collection<UserDto> getAllUsers();
    boolean setNewPassword(NewPasswordDto password);
}
