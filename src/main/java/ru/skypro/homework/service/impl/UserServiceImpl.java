package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repo.UserRepository;
import ru.skypro.homework.service.UserService;

import java.util.Collection;

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

    public UserDto updateUser(String userName,UserDto userDto) throws NotFoundException {
        logger.info("Method updateUser was started");
        User user = userRepository.findUserByUserName(userName);
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        userRepository.save(user);
        logger.info("Mapping from User to UserDto: {}",userMapper.userToUserDto(user));
        return userMapper.userToUserDto(user);
    }

    @Override
    public UserDto getUserById(Long id) throws NotFoundException {
        User foundUser = userRepository.findById(id).orElseThrow(NotFoundException::new);
        return userMapper.userToUserDto(foundUser);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userMapper.entitiesToDto(userRepository.getAll().get());
    }
}
