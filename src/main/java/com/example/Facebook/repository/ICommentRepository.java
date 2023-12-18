package com.example.Facebook.repository;

import com.example.Facebook.models.FacebookComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICommentRepository extends JpaRepository<FacebookComment,Long> {

}
