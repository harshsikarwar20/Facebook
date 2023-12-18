package com.example.Facebook.repository;

import com.example.Facebook.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAdminRepository extends JpaRepository<Admin,Long> {

}
