package com.example.Facebook.repository;

import com.example.Facebook.models.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFollowerRepository extends JpaRepository<Follower,Long> {

}
