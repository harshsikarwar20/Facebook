package com.example.Facebook.services;

import com.example.Facebook.models.Post;
import com.example.Facebook.models.User;
import com.example.Facebook.repository.IPostRepository;
import com.example.Facebook.repository.ITokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    IPostRepository postRepository;

    @Autowired
    ITokenRepository tokenRepository;

    @Autowired
    LikeService likeService;

    public void addPost(Post post) {
        postRepository.save(post);
    }

    public List<Post> getAllPost() {
        return postRepository.findAll();
    }

    public List<Post> getAllPosts(String token) {
        User user = tokenRepository.findFirstByToken(token).getUser();
        List<Post> postList = postRepository.findByUser(user);
        return postList;
    }

    public long getLikes(Long postId) {
        return likeService.getLikes(postId);
    }
}
