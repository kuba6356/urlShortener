package com.urlShortener.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class WebSecurityConfig {

    private static final String[] WHITE_LIST_URLS = {
            "/*",
            "/*/*"
    };
    //creates this object so that it will be accessible outside this class
    @Bean
    //creates a method that encodes the password
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST_URLS).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
