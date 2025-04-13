package com.urlShortener.demo.userFunctionality.repository;

import com.urlShortener.demo.userFunctionality.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

    User findByUsername(String username);

    User findByUserId(Long userId);

}
