package com.urlShortener.demo.userFunctionality.service;

import com.urlShortener.demo.urlFunctionality.service.UrlServiceImpl;
import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.entity.UserPrincipal;
import com.urlShortener.demo.userFunctionality.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsersDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UrlServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(emailOrUsername);
        if(user == null){
            user = userRepository.findByUsername(emailOrUsername);
        }
        UserPrincipal userPrincipal = new UserPrincipal(user);
        if(!user.isActivated()){
            throw new UsernameNotFoundException("Not Activated");
        }
        return userPrincipal;
    }
}
