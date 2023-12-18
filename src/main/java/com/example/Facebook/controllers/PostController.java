package com.example.Facebook.controllers;

import com.example.Facebook.models.Post;
import com.example.Facebook.models.User;
import com.example.Facebook.services.PostService;
import com.example.Facebook.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/Post")
public class PostController {

    @Autowired
    PostService postService;
    @Autowired
    TokenService tokenService;
    @PostMapping()
    private ResponseEntity<String> addPost(@Valid @RequestParam String email , @RequestParam String token , @RequestBody Post post){
        HttpStatus status;
        String message = null;

        if(tokenService.authenticate(email,token)){
            User user = tokenService.findUserByToken(token);
            post.setUser(user); // Setting user in the post...
            postService.addPost(post);
            message = "Post added successfully";
            status = HttpStatus.OK;
        }else{
            message = "Invalid user";
            status = HttpStatus.FORBIDDEN;
        }
        return new ResponseEntity<String>(message,status);
    }

    @GetMapping()
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam String email , @RequestParam String token){
        HttpStatus status;
        List<Post> postList = null;
        if(tokenService.authenticate(email,token)) {
            postList = postService.getAllPosts(token);
            status = HttpStatus.OK;
        }
        else {
            status = HttpStatus.FORBIDDEN;
        }
        return new ResponseEntity<List<Post>>(postList , status);
    }

    @GetMapping("/{postId}/likeCount")
    long getLikesForPost(@PathVariable Long postId) {
        return postService.getLikes(postId);
    }

}