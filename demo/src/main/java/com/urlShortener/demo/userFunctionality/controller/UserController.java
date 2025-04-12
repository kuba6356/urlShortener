package com.urlShortener.demo.userFunctionality.controller;

import com.urlShortener.demo.urlFunctionality.controller.UrlController;
import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.repository.UserRepository;
import com.urlShortener.demo.userFunctionality.service.JwtService;
import com.urlShortener.demo.userFunctionality.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody User user){
        if(user.getEmail().equals("") || user.getUsername().equals("")){
            return new ResponseEntity<>("Your email and username can't be left blank", HttpStatus.BAD_REQUEST);
        }
        User newUser = userService.registerUser(user);
        return new ResponseEntity<>("Please confirm your email via the link sent to your inboxz", HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateEmail(@RequestParam("code") String code ){
        if(code.isEmpty()){
            return new ResponseEntity<>("This verification code doesn't exist", HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>("Wrong email/username or password", HttpStatus.NOT_FOUND);
        }
        httpServletResponse.setHeader("JWT", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id){
        User user = userService.findUserById(id);
        if(user == null){
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
         return new ResponseEntity<>(user, HttpStatus.OK) ;
    }
    @PutMapping("/passwordChange")
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword  ,@RequestParam String newPassword, HttpServletResponse httpServletResponse){
        User loggedInUser = userRepository.findByUsername(jwtService.extractUserName(httpServletResponse.getHeader("Authorization")));
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
            return new ResponseEntity<>("we suggest you register before trying to reset your password", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("resetPasswordToken")
    public ResponseEntity<String> resetPasswordValidateCode(@RequestParam("code") String code, @RequestParam String newPassword){
        String response = userService.checkPasswordValidationCode(code, newPassword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
