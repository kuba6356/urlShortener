package com.urlShortener.demo.userFunctionality.service;

import com.urlShortener.demo.urlFunctionality.controller.UrlController;
import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.entity.ValidationToken;
import com.urlShortener.demo.userFunctionality.repository.UserRepository;
import com.urlShortener.demo.userFunctionality.repository.ValidationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Calendar;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UrlController urlController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ValidationTokenRepository validationTokenRepository;
    @Override
    public void sentValidationEmail(ValidationToken validationToken, User user){
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("kuba6356@gmail.com");
        mail.setTo(user.getEmail());
        mail.setSubject("Registration validation");
        mail.setText(("<html>" +
                "<body>" +
                        "<p>Hi "+ user.getUsername() +",</p>" +
                        "<p>Please verify your email by clicking this link:<br>" +
                        "<a href=\"http://localhost:8080/validate?code=" + validationToken.getToken() + "\">Verify Email</a></p>" +
                        "<p>This link expires in 20 minutes.</p>" +
                        "<p>The Example Team</p>" +
                        "</body>" +
                        "</html>"));
        javaMailSender.send(mail);

    }
    public User registerUser(User user) {
        user.setPasswordHash(encoder.encode(user.getPasswordHash()));
        ValidationToken validationToken = new ValidationToken(user);
        userRepository.save(user);
        validationTokenRepository.save(validationToken);
        sentValidationEmail(validationToken, user);
        return user;
    }

    @Override
    @Transactional
    public String validateNewUser(String code) {
        if(validationTokenRepository.existsByToken(code)){
           ValidationToken token = validationTokenRepository.findByToken(code);
           User user = token.getUser();
           if(token.getExpirationDate().getTime() - Calendar.getInstance().getTime().getTime() <= 0){
                validationTokenRepository.delete(token);
                token = new ValidationToken(user);
                validationTokenRepository.save(token);
                return "Token expired. we've sent you a new token please check your inbox";
           }
           user.changeToActive();
           userRepository.save(user);
           urlController.setLoggedInUser(user);
           validationTokenRepository.deleteByToken(code);
            return "User has been validated and singed in";

        }
        return "Your token doesn't exist";
    }

    @Override
    public String login(String emailOrUsername, String password) {
        User user = userRepository.findByEmail(emailOrUsername);
        if(user == null){
            user = userRepository.findByUsername(emailOrUsername);
        }
        if(!user.isActivated()){
            return "Not Activated";
        }
        if(!encoder.matches(password, user.getPasswordHash())){
            return "Invalid";
        }
        urlController.setLoggedInUser(user);
        return "User successfully logged in";
    }

    @Override
    public User findUserById(Long id){
        return userRepository.findById(id).get();
    }

    @Override
    public String changePasswordWithExisting(String newPassword, String oldPassword, User loggedInUser) {
        if(!encoder.matches(oldPassword, loggedInUser.getPasswordHash())){
            return "Please input the correct password to change it";
        }
        User user = userRepository.findByUserId(loggedInUser.getUserId());
        user.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(user);
        return "Your password has been changed";
    }


}
