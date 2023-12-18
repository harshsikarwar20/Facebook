package com.example.Facebook.repository;

import com.example.Facebook.models.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILikeRepository extends JpaRepository<PostLike,Long> {
    long countByPost_PostId(Long postId);
}
