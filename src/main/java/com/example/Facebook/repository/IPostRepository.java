package com.example.Facebook.repository;

import com.example.Facebook.models.Post;
import com.example.Facebook.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post,Integer> {

    List<Post> findByUser(User user);

}
