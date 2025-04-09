package com.urlShortener.demo.userFunctionality.controller;

import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody User user){
        if(user.getEmail().equals("") || user.getUsername().equals("")){
            return new ResponseEntity<>("Your email and username can't be left blank", HttpStatus.BAD_REQUEST);
        }
        User newUser = userService.registerUser(user);
        return new ResponseEntity<>("Please confirm your email via the link sent to your inboxz", HttpStatus.OK);
    }
}
