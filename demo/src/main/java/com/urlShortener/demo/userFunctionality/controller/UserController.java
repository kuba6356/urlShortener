package com.urlShortener.demo.userFunctionality.controller;

import com.urlShortener.demo.errorhadnling.ErrorMessages;
import com.urlShortener.demo.errorhadnling.exceptions.InvalidJwtException;
import com.urlShortener.demo.errorhadnling.exceptions.UserNotFoundException;
import com.urlShortener.demo.urlFunctionality.service.UrlServiceImpl;
import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.repository.UserRepository;
import com.urlShortener.demo.userFunctionality.service.JwtService;
import com.urlShortener.demo.userFunctionality.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UrlServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ErrorMessages errorMessages;

    @ModelAttribute("loggedInUser")
    public User loggedInUser(HttpServletRequest request){
        try{
            if(request.getHeader("Authorization") == null || request.getHeader("Authorization").isEmpty()){
                return null;
            }
            String authHeader = request.getHeader("Authorization").substring(7);

            String username = jwtService.extractUserName(authHeader);
            User loggedInUser = userRepository.findByUsername(username);
            if(loggedInUser == null){
                loggedInUser = userRepository.findByEmail(username);
            }
            return loggedInUser;
        }catch (Exception e){
            throw new InvalidJwtException("Invalid Token");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody User user){
        if(user.getEmail().equals("") || user.getUsername().equals("")){
            return new ResponseEntity<>(errorMessages.RECEIVED_EMPTY_ON_EXPECTED_VALUE, HttpStatus.BAD_REQUEST);
        }
        User newUser = userService.registerUser(user);
        return new ResponseEntity<>("Please confirm your email via the link sent to your inbox", HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateEmail(@RequestParam("code") String code ){
        if(code.isEmpty()){
            return new ResponseEntity<>(errorMessages.VALUE_DOESNT_EXIST, HttpStatus.NOT_FOUND);
        }
       String returnCode =  userService.validateNewUser(code);
        if(returnCode.equalsIgnoreCase("invalid") || returnCode.equalsIgnoreCase("Token expired. we've sent you a new token please check your inbox")){
                return new ResponseEntity<>(returnCode, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(returnCode, HttpStatus.OK);
}
    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestBody User user, HttpServletResponse httpServletResponse){
        String response = userService.login(user);
        if(response.equalsIgnoreCase("invalid")){
            return new ResponseEntity<>(errorMessages.INVALID_USER, HttpStatus.NOT_FOUND);
        }
        httpServletResponse.setHeader("JWT", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id){
        User user = userService.findUserById(id);
        if(user == null){
            throw new UserNotFoundException(errorMessages.VALUE_DOESNT_EXIST);
        }
         return new ResponseEntity<>(user, HttpStatus.OK) ;
    }
    @PutMapping("/passwordChange")
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword  , @RequestParam String newPassword, @ModelAttribute("loggedInUser") User loggedInUser){
        String response = userService.changePasswordWithExisting(newPassword, oldPassword, loggedInUser);
        if(response.equalsIgnoreCase("Please input the correct password to change it")){
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam String emailOrUsername){
        String response = userService.ressetPasswordWithToken(emailOrUsername);
        if(response.equalsIgnoreCase("Not found")){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("resetPasswordToken")
    public ResponseEntity<String> resetPasswordValidateCode(@RequestParam("code") String code, @RequestParam String newPassword){
        String response = userService.checkPasswordValidationCode(code, newPassword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
