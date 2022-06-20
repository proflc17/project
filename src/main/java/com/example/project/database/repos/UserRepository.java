package com.example.project.database.repos;

import com.example.project.database.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT distinct u FROM UserEntity u WHERE u.email = ?1 AND u.password = ?2")
    UserEntity findByEmailAndPassword(String email, String password);

    @Query("SELECT distinct u FROM UserEntity u WHERE lower(concat(u.firstname, ' ', u.lastname)) LIKE lower(concat('%', ?1, '%') ) ")
    List<UserEntity> findUsersByName(String name);

    @Query("SELECT distinct u FROM UserEntity u WHERE u.username = ?1")
    UserEntity findUsersByUsername(String username);
}
