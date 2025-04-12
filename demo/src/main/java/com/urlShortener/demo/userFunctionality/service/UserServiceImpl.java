package com.urlShortener.demo.userFunctionality.service;

import com.urlShortener.demo.userFunctionality.entity.PasswordValidationToken;
import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.entity.UserPrincipal;
import com.urlShortener.demo.userFunctionality.entity.ValidationToken;
import com.urlShortener.demo.userFunctionality.repository.PasswordValidationTokenRepository;
import com.urlShortener.demo.userFunctionality.repository.UserRepository;
import com.urlShortener.demo.userFunctionality.repository.ValidationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Calendar;

@Service
public class UserServiceImpl implements UserService {

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

    @Override
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder(12);
    }


    @Override
    public void sentValidationEmail(ValidationToken validationToken, User user){
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("kuba6356@gmail.com");
        mail.setTo(user.getEmail());
        mail.setSubject("Registration validation");
        mail.setText((
                        "\nHi "+ user.getUsername() +"," +
                        "\nPlease verify your email by clicking this link:" +
                        "    http://localhost:8080/validate?code=" + validationToken.getToken() +
                        "\nThis link expires in 20 minutes.</p>" +
                        "\n" +
                        "\nThe Example Team</p>"));
        javaMailSender.send(mail);

    }
    public User registerUser(User user) {
        user.setPasswordHash(encoder().encode(user.getPassword()));
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
           validationTokenRepository.deleteByToken(code);
            return "User has been validated and singed in";

        }
        return "Your token doesn't exist";
    }

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

    @Override
    public String changePasswordWithExisting(String newPassword, String oldPassword, User loggedInUser) {
        if(!encoder().matches(oldPassword, loggedInUser.getPassword())){
            return "Please input the correct password to change it";
        }
        User user = userRepository.findByUserId(loggedInUser.getUserId());
        user.setPasswordHash(encoder().encode(newPassword));
        userRepository.save(user);
        return "Your password has been changed";
    }

    @Override
    public String ressetPasswordWithToken(String emailOrUsername) {
        User user = userRepository.findByEmail(emailOrUsername);
        if(user == null){
            user = userRepository.findByUsername(emailOrUsername);
        }
        if(user == null){
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
                "\nHi "+ user.getUsername() +"," +
                "\nHere is a link to change your password:" +
                "   http://localhost:8080/resetPassword?code=" + passwordValidationToken.getToken() +
                "\nThis link expires in 20 minutes." +
                "\nIf you haven't asked to reset your password please ignore this email" +
                "\n" +
                "\nThe Example Team"));
        javaMailSender.send(mail);
        return "Email with reset password link sent";
    }

    @Transactional
    @Override
    public String checkPasswordValidationCode(String code, String password) {
        if(passwordValidationTokenRepository.existsByToken(code)){
            PasswordValidationToken token = passwordValidationTokenRepository.findByToken(code);
            User user = token.getUser();
            if(token.getExpirationDate().getTime() - Calendar.getInstance().getTime().getTime() <= 0){
                passwordValidationTokenRepository.delete(token);
                token = new PasswordValidationToken(user);
                passwordValidationTokenRepository.save(token);
                return "Token expired. we've sent you a new token please check your inbox";
            }
            user.setPasswordHash(encoder().encode(password));
            userRepository.save(user);
            passwordValidationTokenRepository.deleteByToken(code);
            return "Password has been changed";

        }
        return "Your token doesn't exist";
    }


}
