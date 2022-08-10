package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skypro.homework.controller.UserController;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repo.UserRepository;
import ru.skypro.homework.service.UserService;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto addUser(CreateUserDto createUser){
        User user = userMapper.createUserDtoToUser(createUser);
        userRepository.save(user);
        return userMapper.userToUserDto(user);
    }

    public UserDto updateUser(UserDto userDto) throws NotFoundException {
        logger.info("Method updateUser was started");
        User user = userRepository.findById(userDto.getId()).orElseThrow(NotFoundException::new);

        userRepository.save(userMapper.userDtoToUser(userDto));
        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) throws NotFoundException {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        return userMapper.userToUserDto(user);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userMapper.entitiesToDto(userRepository.findAll());
    }
}
