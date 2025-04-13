package com.urlShortener.demo.userFunctionality.service;

import com.urlShortener.demo.errorhadnling.exceptions.EmailException;
import com.urlShortener.demo.urlFunctionality.service.UrlServiceImpl;
import com.urlShortener.demo.userFunctionality.entity.PasswordValidationToken;
import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.entity.ValidationToken;
import com.urlShortener.demo.userFunctionality.repository.PasswordValidationTokenRepository;
import com.urlShortener.demo.userFunctionality.repository.UserRepository;
import com.urlShortener.demo.userFunctionality.repository.ValidationTokenRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UrlServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ValidationTokenRepository validationTokenRepository;

    @Autowired
    private PasswordValidationTokenRepository passwordValidationTokenRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public PasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Value("${spring.mail.username}")
    private String email;


    @Override
    public void sentValidationEmail(ValidationToken validationToken, User user) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(email);
            mail.setTo(user.getEmail());
            mail.setSubject("Registration validation");
            mail.setText((
                    "Hi " + user.getUsername() + "," +
                            "\n\nPlease verify your email by clicking this link:" +
                            "    http://localhost:8080/validate?code=" + validationToken.getToken() +
                            "\n\nThis link expires in 20 minutes." +
                            "\n\nThe Example Team"));
            javaMailSender.send(mail);
        } catch (Exception e) {
            throw new EmailException("Email unavailable");
        }
    }

    @Transactional
    @Override
    public User registerUser(User user) {
        user.setPasswordHash(encoder.encode(user.getPassword()));
        ValidationToken validationToken = new ValidationToken(user);
        userRepository.save(user);
        validationTokenRepository.save(validationToken);
        sentValidationEmail(validationToken, user);
        return user;
    }

    @Transactional
    @Override
    public String validateNewUser(String code) {
        if(validationTokenRepository.existsByToken(code)){
           ValidationToken token = validationTokenRepository.findByToken(code);
           User user = token.getUser();
           if(Instant.now().isAfter(token.getExpirationDate())){
                validationTokenRepository.delete(token);
                token = new ValidationToken(user);
                validationTokenRepository.save(token);
                return "Token expired. we've sent you a new token please check your inbox";
           }
           user.changeToActive();
           userRepository.save(user);
           validationTokenRepository.deleteByToken(code);
            return "User has been validated and singed in";

        }
        return "Your token doesn't exist";
    }

    @Transactional
    @Override
    public String login(User user) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if(authentication.isAuthenticated()){
            User loggedUser = userRepository.findByEmail(user.getUsername());
            if(loggedUser == null){
                loggedUser = userRepository.findByUsername(user.getUsername());
            }
            return jwtService.generateToken(loggedUser.getUsername());
        }
        return "invalid";
    }

    @Override
    public User findUserById(Long id){
        return userRepository.findById(id).get();
    }

    @Transactional
    @Override
    public String changePasswordWithExisting(String newPassword, String oldPassword, User loggedInUser) {
        if(!encoder.matches(oldPassword, loggedInUser.getPassword())){
            return "Please input the correct password to change it";
        }
        User user = userRepository.findByUserId(loggedInUser.getUserId());
        user.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(user);
        return "Your password has been changed";
    }

    @Transactional
    @Override
    public String ressetPasswordWithToken(String emailOrUsername) {
        try {
            User user = userRepository.findByEmail(emailOrUsername);
            if (user == null) {
                user = userRepository.findByUsername(emailOrUsername);
            }
            if (user == null) {
                return "Not found";
            }
            PasswordValidationToken passwordValidationToken = new PasswordValidationToken(user);
            passwordValidationTokenRepository.save(passwordValidationToken);
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom("kuba6356@gmail.com");
            mail.setTo(user.getEmail());
            mail.setSubject("Password reset");
            mail.setText((
                    "\n" +
                            "\nHi " + user.getUsername() + "," +
                            "\nHere is a link to change your password:" +
                            "   http://localhost:8080/resetPasswordToken?code=" + passwordValidationToken.getToken() +
                            "\nThis link expires in 20 minutes." +
                            "\nIf you haven't asked to reset your password please ignore this email" +
                            "\n" +
                            "\nThe Example Team"));
            javaMailSender.send(mail);
            return "Email with reset password link sent";
        } catch (Exception e) {
            throw new EmailException("Email unavailable");
        }
    }

    @Transactional
    @Override
    public String checkPasswordValidationCode(String code, String password) {
        if(passwordValidationTokenRepository.existsByToken(code)){
            PasswordValidationToken token = passwordValidationTokenRepository.findByToken(code);
            User user = token.getUser();
            if(Instant.now().isAfter(token.getExpirationDate())){
                passwordValidationTokenRepository.delete(token);
                token = new PasswordValidationToken(user);
                passwordValidationTokenRepository.save(token);
                return "Token expired. we've sent you a new token please check your inbox";
            }
            user.setPasswordHash(encoder.encode(password));
            userRepository.save(user);
            passwordValidationTokenRepository.deleteByToken(code);
            return "Password has been changed";

        }
        return "Your token doesn't exist";
    }


}
