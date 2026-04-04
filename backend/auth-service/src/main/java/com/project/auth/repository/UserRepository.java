package com.project.auth.repository;

import com.project.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 아이디 중복 체크
    boolean existsByUserId(String userId);

    Optional<User> findByUserId(String userId);

}
