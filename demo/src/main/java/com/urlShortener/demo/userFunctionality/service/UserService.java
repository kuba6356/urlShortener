package com.urlShortener.demo.userFunctionality.service;

import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.entity.ValidationToken;

public interface UserService {

    void sentValidationEmail(ValidationToken validationToken, User user);

    User registerUser(User user);

    String validateNewUser(String code);

    String login(String emailOrUsername, String password);

    User findUserById(Long id);

    String changePasswordWithExisting(String password, String oldPassword, User loggedInUser);
}
