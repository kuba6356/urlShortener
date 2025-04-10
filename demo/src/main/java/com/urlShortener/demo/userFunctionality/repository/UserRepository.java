package com.urlShortener.demo.userFunctionality.repository;

import com.urlShortener.demo.urlFunctionality.entity.Url;
import com.urlShortener.demo.userFunctionality.entity.User;
import com.urlShortener.demo.userFunctionality.entity.ValidationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String emailOrUsername);

    User findByUsername(String emailOrUsername);

    User findByUrl(Url url);

    User findByUserId(Long userId);

}
