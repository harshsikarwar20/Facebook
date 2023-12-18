package com.example.Facebook.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    UserService userService;

    public String toggleBlueTick(Long id, boolean blueTick) {
        return userService.toggleBlueTick(id,blueTick);
    }
}
