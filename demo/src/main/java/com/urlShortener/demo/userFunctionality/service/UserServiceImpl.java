package com.urlShortener.demo.userFunctionality.service;

import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerUser(User user) {
        userRepository.save(user);
        return user;
    }
}
