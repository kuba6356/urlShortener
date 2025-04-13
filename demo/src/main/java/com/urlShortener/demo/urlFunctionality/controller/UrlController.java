package com.urlShortener.demo.urlFunctionality.controller;

import com.urlShortener.demo.errorhadnling.ErrorMessages;
import com.urlShortener.demo.errorhadnling.exceptions.ContentUnavailableInvalidUserException;
import com.urlShortener.demo.errorhadnling.exceptions.InvalidJwtException;
import com.urlShortener.demo.urlFunctionality.entity.Url;
import com.urlShortener.demo.urlFunctionality.service.UrlService;
import com.urlShortener.demo.urlFunctionality.service.UrlServiceImpl;
import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.repository.UserRepository;
import com.urlShortener.demo.userFunctionality.service.JwtService;
import com.urlShortener.demo.userFunctionality.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import static com.urlShortener.demo.errorhadnling.ErrorMessages.INVALID_USER;

@RestController
public class UrlController {
    private static final Logger log = LoggerFactory.getLogger(UrlServiceImpl.class);

    @Autowired
    private UrlService urlService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;
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

            //POST

    @PostMapping("/create")

    public ResponseEntity<String> createNewShortUrl(@RequestParam String longUrl, @ModelAttribute("loggedInUser") User loggedInUser){
            if(loggedInUser == null){
                return new ResponseEntity<>(ErrorMessages.TRY_BEFORE_AUTHENTICATION, HttpStatus.UNAUTHORIZED);
            }
            if (longUrl == null || longUrl.trim().isEmpty()) {
                return new ResponseEntity<>(ErrorMessages.RECEIVED_EMPTY_ON_EXPECTED_VALUE, HttpStatus.BAD_REQUEST);
            }
            String response = urlService.urlCreateService(longUrl, loggedInUser);
            return new ResponseEntity<>(response, HttpStatus.CREATED);


    }

    //UPDATE

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUrl(@PathVariable Long id, @RequestParam String newLongUrl, @ModelAttribute("loggedInUser") User loggedInUser){
        if(loggedInUser == null){
            return new ResponseEntity<>(ErrorMessages.TRY_BEFORE_AUTHENTICATION, HttpStatus.UNAUTHORIZED);
        }
            if ((id == null) || (newLongUrl == null || newLongUrl.trim().isEmpty())) {
                return new ResponseEntity<>(ErrorMessages.RECEIVED_EMPTY_ON_EXPECTED_VALUE, HttpStatus.BAD_REQUEST);
            }
            String returnStatus = urlService.updateUrl(id, newLongUrl, loggedInUser);
            if(returnStatus.equalsIgnoreCase("Invalid User")){
                return new ResponseEntity<>(INVALID_USER, HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(returnStatus,HttpStatus.OK);

    }

    //DELETE

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUrl(@PathVariable Long id, @ModelAttribute("loggedInUser") User loggedInUser){
        if(loggedInUser == null){
            return new ResponseEntity<>(ErrorMessages.TRY_BEFORE_AUTHENTICATION, HttpStatus.UNAUTHORIZED);
        }
            if(id== null){
                return new ResponseEntity<>(ErrorMessages.VALUE_DOESNT_EXIST, HttpStatus.NOT_FOUND);
            }

            String returnStatus = urlService.deleteUrl(id, loggedInUser);
            if(returnStatus.equalsIgnoreCase("Invalid User")){
                return new ResponseEntity<>(INVALID_USER, HttpStatus.UNAUTHORIZED);
            }
            if(returnStatus.equalsIgnoreCase("Not Activated")){
                return new ResponseEntity<>(ErrorMessages.ACTIVATE_ACCOUNT, HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(returnStatus, HttpStatus.OK);
    }

    //GET

    @GetMapping("/get/{id}")
    public ResponseEntity<Url> getUrl(@PathVariable Long id, @ModelAttribute("loggedInUser") User loggedInUser){
        Url url = urlService.getUrl(id, loggedInUser);
        if(url == null){
            throw new ContentUnavailableInvalidUserException(INVALID_USER);
        }
        return new ResponseEntity<>(url, HttpStatus.OK);

    }

    //REDIRECT

    @RequestMapping("/r/{id}")
    public RedirectView RedirectToLongLink(@PathVariable Long id, HttpServletRequest request){
        return new RedirectView(urlService.redirectClick(id, request));
    }

}
