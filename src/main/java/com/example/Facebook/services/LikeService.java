package com.example.Facebook.services;

import com.example.Facebook.models.PostLike;
import com.example.Facebook.repository.ILikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    ILikeRepository likeRepository;

    public void like(PostLike postLike) {
        likeRepository.save(postLike);
    }

    public long getLikes(Long postId) {
        //todo : validation to be added
        return likeRepository.countByPost_PostId(postId);
    }
}
