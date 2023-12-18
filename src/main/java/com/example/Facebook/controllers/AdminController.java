package com.example.Facebook.controllers;

import com.example.Facebook.models.Post;
import com.example.Facebook.models.User;
import com.example.Facebook.services.AdminService;
import com.example.Facebook.services.PostService;
import com.example.Facebook.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/Admin")
public class AdminController {
    @Autowired
    AdminService adminService;
    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @GetMapping(value = "/users")
    private List<User> getAllUser(){
        return userService.getAllUser();
    }

    @GetMapping(value = "/post")
    private List<Post> getAllPost(){
        return postService.getAllPost();
    }

    @DeleteMapping(value = "/delete/user/{userId}")
    private String deleteUserById(@PathVariable Long userId){
        return userService.deleteById(userId);
    }

    @PutMapping(value = "user/{id}/{blueTick}")
    private String toggleBlueTick(@PathVariable Long id , @PathVariable boolean blueTick){
        return adminService.toggleBlueTick(id,blueTick);
    }
}
