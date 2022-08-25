package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CreateUserDto;
import ru.skypro.homework.dto.NewPasswordDto;
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
    private final AuthServiceImpl authService;
    private final PasswordEncoder encoder;
    private final UserDetailsManager manager;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, AuthServiceImpl authService, UserDetailsManager manager) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authService = authService;
        this.encoder = new BCryptPasswordEncoder();
        this.manager = manager;
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

    @Override
    public boolean setNewPassword(NewPasswordDto password) {
        User user = userRepository.getUserById(authService.getIdCurrentUser());
        String encryptedPassword = user.getPassword();
        String encryptedPasswordWithoutEncryptionType = encryptedPassword.substring(8);
        if (!encoder.matches(password.getCurrentPassword(), encryptedPasswordWithoutEncryptionType)) {
            return false;
        }
        manager.createUser(
                org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder()
                        .password(password.getNewPassword())
                        .username(user.getUserName())
                        .roles(user.getRole().name())
                        .build()
        );
        UserDetails userDetails = manager.loadUserByUsername(user.getUserName());
        String encryptedNewPassword = userDetails.getPassword();
        user.setPassword(encryptedNewPassword);
        userRepository.save(user);
        return true;
    }
}
