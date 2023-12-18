package com.example.Facebook.repository;

import com.example.Facebook.models.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFollowingRepository extends JpaRepository<Following,Long> {

}
