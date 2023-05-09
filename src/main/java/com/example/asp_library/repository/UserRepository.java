package com.example.asp_library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.asp_library.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByStudentId(String studentId);
}
