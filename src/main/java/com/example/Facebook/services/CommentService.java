package com.example.Facebook.services;

import com.example.Facebook.models.FacebookComment;
import com.example.Facebook.repository.ICommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    ICommentRepository commentRepository;
    public String addComment(FacebookComment comment) {
        commentRepository.save(comment);
        return "Comment added to this post!!";
    }
}
