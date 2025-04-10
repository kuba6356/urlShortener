package com.urlShortener.demo.urlFunctionality.controller;

import com.urlShortener.demo.urlFunctionality.entity.Url;
import com.urlShortener.demo.urlFunctionality.service.UrlService;
import com.urlShortener.demo.userFunctionality.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class UrlController {

    @Autowired
    private UrlService urlService;

    private User loggedInUser;

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
    //POST

    @PostMapping("/create")

    public ResponseEntity<String> createNewShortUrl(@RequestParam String longUrl){
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
    public ResponseEntity<String> updateUrl(@PathVariable Long id, @RequestParam String newLongUrl){
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
    public ResponseEntity<String> deleteUrl(@PathVariable Long id){
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

    @RequestMapping("{id}")
    public RedirectView RedirectToLongLink(@PathVariable Long id, HttpServletRequest request){
        return new RedirectView(urlService.redirectClick(id, request));
    }

}
