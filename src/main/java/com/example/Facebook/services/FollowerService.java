package com.example.Facebook.services;

import com.example.Facebook.models.Follower;
import com.example.Facebook.models.User;
import com.example.Facebook.repository.IFollowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowerService {
    @Autowired
    IFollowerRepository followerRepository;

    public void saveFollower(User otherUser, User myUser) {
        Follower follower = new Follower(null,myUser,otherUser);
        followerRepository.save(follower);
    }
}
