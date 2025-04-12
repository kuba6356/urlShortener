package com.urlShortener.demo.urlFunctionality.controller;

import com.urlShortener.demo.urlFunctionality.entity.Url;
import com.urlShortener.demo.urlFunctionality.service.UrlService;
import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.repository.UserRepository;
import com.urlShortener.demo.userFunctionality.service.JwtService;
import com.urlShortener.demo.userFunctionality.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class UrlController {

    @Autowired
    private UrlService urlService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    //POST

    @PostMapping("/create")

    public ResponseEntity<String> createNewShortUrl(@RequestParam String longUrl, HttpServletResponse httpServletResponse){
            User loggedInUser = userRepository.findByUsername(jwtService.extractUserName(httpServletResponse.getHeader("Authorization")));
            User user = null;
            if(loggedInUser == null){
                return new ResponseEntity<>("you must login first to create a new url", HttpStatus.FORBIDDEN);
            }
            if (longUrl == null || longUrl.trim().isEmpty()) {
                return new ResponseEntity<>("provided link cannot be empty", HttpStatus.BAD_REQUEST);
            }
            String response = urlService.urlCreateService(longUrl, loggedInUser);
            return new ResponseEntity<>(response, HttpStatus.CREATED);


    }

    //UPDATE

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUrl(@PathVariable Long id, @RequestParam String newLongUrl, HttpServletResponse response){
        User loggedInUser = userRepository.findByUsername(jwtService.extractUserName(response.getHeader("Authorization")));
        if(loggedInUser == null){
            return new ResponseEntity<>("you must login first to create a new url", HttpStatus.FORBIDDEN);
        }
            if ((id == null) || (newLongUrl == null || newLongUrl.trim().isEmpty())) {
                return new ResponseEntity<>("provided links cannot be empty", HttpStatus.BAD_REQUEST);
            }
            String returnStatus = urlService.updateUrl(id, newLongUrl, loggedInUser);
            if(returnStatus.equalsIgnoreCase("Invalid User")){
                return new ResponseEntity<>("Please provide a link that is related to your account", HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>("Url has been updated successfully",HttpStatus.OK);

    }

    //DELETE

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUrl(@PathVariable Long id, HttpServletResponse response){
        User loggedInUser = userRepository.findByUsername(jwtService.extractUserName(response.getHeader("Authorization")));
        if(loggedInUser == null){
            return new ResponseEntity<>("you must login first to create a new url", HttpStatus.FORBIDDEN);
        }
            if(id== null){
                return new ResponseEntity<>("Sorry This link doesn't exist", HttpStatus.NOT_FOUND);
            }

            String returnStatus = urlService.deleteUrl(id, loggedInUser);
            if(returnStatus.equalsIgnoreCase("Invalid User")){
                return new ResponseEntity<>("Please provide a link that is related to your account", HttpStatus.FORBIDDEN);
            }
            if(returnStatus.equalsIgnoreCase("Not Activated")){
                return new ResponseEntity<>("Please activate your account first", HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>("Url Deleted Successfully", HttpStatus.OK);
    }

    //GET

    @GetMapping("/get/{id}")
    public ResponseEntity<Url> getUrl(@PathVariable Long id){
        return new ResponseEntity<>(urlService.findById(id), HttpStatus.OK);

    }

    //REDIRECT

    @RequestMapping("/r/{id}")
    public RedirectView RedirectToLongLink(@PathVariable Long id, HttpServletRequest request){
        return new RedirectView(urlService.redirectClick(id, request));
    }

}
