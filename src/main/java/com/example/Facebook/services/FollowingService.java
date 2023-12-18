package com.example.Facebook.services;

import com.example.Facebook.models.Following;
import com.example.Facebook.models.User;
import com.example.Facebook.repository.IFollowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowingService {

    @Autowired
    IFollowingRepository followingRepository;

    public void saveFollowing(User myUser, User otherUser) {
        Following followingRecord = new Following(null,myUser,otherUser);
        followingRepository.save(followingRecord);
    }
}
