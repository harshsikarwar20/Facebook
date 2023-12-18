package com.example.Facebook.controllers;

import com.example.Facebook.models.FacebookComment;
import com.example.Facebook.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/comment")
public class CommentController {
    @Autowired
    CommentService commentService;

    @PostMapping()
    public String addComment(@RequestBody FacebookComment comment){
        return commentService.addComment(comment);
    }
}
