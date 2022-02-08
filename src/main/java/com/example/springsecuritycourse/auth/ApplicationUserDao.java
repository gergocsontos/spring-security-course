package com.example.springsecuritycourse.auth;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // not added in video
public interface ApplicationUserDao {
    Optional<ApplicationUser> selectApplicationUserByUsername(String username);
}
