package com.example.Facebook.controllers;

import com.example.Facebook.dto.SignInInput;
import com.example.Facebook.dto.SignInOutput;
import com.example.Facebook.dto.SignUpOutput;
import com.example.Facebook.models.PostLike;
import com.example.Facebook.models.User;
import com.example.Facebook.services.TokenService;
import com.example.Facebook.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    public UserService userService;

    @Autowired
    public TokenService tokenService;


    @PostMapping(value = "/signUp")
    public SignUpOutput signUp(@Valid @RequestBody User signUpDto){
        return userService.signUp(signUpDto);
    }


    @PostMapping(value = "/signIn")
    public SignInOutput signIn(@Valid @RequestBody SignInInput signInDto){
        return userService.signIn(signInDto);
    }


    @DeleteMapping(value = "/signOut")
    public ResponseEntity<String> signOut(@RequestParam String email , @RequestParam String token){
        HttpStatus status = null;
        String message = null;

        if(tokenService.authenticate(email,token)){
            tokenService.deleteToken(token);
            message = "Sign Out Successful";
            status = HttpStatus.OK;
        }else{
            message = "Invalid User";
            status = HttpStatus.FORBIDDEN;
        }
        return new ResponseEntity<String>(message,status);
    }

    @PutMapping()
    public ResponseEntity<String> updateUser(@RequestBody String email , @RequestParam String token , @RequestBody User user){
        HttpStatus status;
        String message = null;

        if(tokenService.authenticate(email,token)){
            try{
                userService.updateUser(user,token);
                status = HttpStatus.OK;
                message = "User Updated successfully";
            }catch(Exception e){
                status = HttpStatus.BAD_REQUEST;
                message = "Enter valid Information";
            }
        }else{
            status = HttpStatus.FORBIDDEN;
            message = "Invalid User";
        }
        return new ResponseEntity<String>(message,status);
    }

    @PostMapping(value = "/follow/{userId}/{otherId}")
    private String followUser(@PathVariable Long userId , @PathVariable Long otherId){
        return userService.followUser(userId,otherId);
    }

    @PostMapping("/like")
    void likePost(@RequestBody PostLike postLike){
        //todo : validation
        userService.like(postLike);
    }
}
