package com.urlShortener.demo.userFunctionality.service;

import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.entity.ValidationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface UserService {

    void sentValidationEmail(ValidationToken validationToken, User user);

    User registerUser(User user);

    String validateNewUser(String code);

    String login(User user);

    User findUserById(Long id);

    String changePasswordWithExisting(String password, String oldPassword, User loggedInUser);

    String ressetPasswordWithToken(String emailOrUsername);

    String checkPasswordValidationCode(String code, String password);


}
