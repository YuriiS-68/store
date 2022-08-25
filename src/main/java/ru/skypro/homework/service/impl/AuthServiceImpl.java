package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.repo.UserRepository;
import ru.skypro.homework.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;

    private final PasswordEncoder encoder;

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;

    public AuthServiceImpl(UserDetailsManager manager, UserRepository userRepository) {
        this.manager = manager;
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean login(String userName, String password) {
        if (!userRepository.existsUserByUserName(userName)) {
            return false;
        }
        ru.skypro.homework.model.User user = userRepository.getUserByUserName(userName);
        String encryptedPassword = user.getPassword();
        String encryptedPasswordWithoutEncryptionType = encryptedPassword.substring(8);
        return encoder.matches(password, encryptedPasswordWithoutEncryptionType);
    }

   /* @Override
    public boolean login(String userName, String password) {
        if (!userRepository.existsUserByUserName(userName)) {
            return false;
        }
        ru.skypro.homework.model.User user = userRepository.getUserByUserName(userName);
        String usersPassword = user.getPassword();
        return password.equals(usersPassword);
    }*/

    @Override
    public boolean register(RegisterReq registerReq, Role role) {
        if (manager.userExists(registerReq.getUsername())) {
            return false;
        }
        manager.createUser(
                User.withDefaultPasswordEncoder()
                        .password(registerReq.getPassword())
                        .username(registerReq.getUsername())
                        .roles(role.name())
                        .build()
        );
        logger.info("User was saved: {}",manager.loadUserByUsername(registerReq.getUsername()));
        return true;
    }

    public Long getIdCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        Long idUser = userRepository.getIdByUserName(currentUserName);
        return idUser;
    }

    @Override
    public String getEncryptedPassword(RegisterReq registerReq) {
        UserDetails userDetails = manager.loadUserByUsername(registerReq.getUsername());
        return userDetails.getPassword();
    }


}
