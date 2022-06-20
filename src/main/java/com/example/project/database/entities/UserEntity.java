package com.example.project.database.entities;

import lombok.*;
import org.apache.catalina.User;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;

    @ToString.Exclude
    @JoinTable(name = "Friends", joinColumns = {
            @JoinColumn(name = "userId", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "friendId", referencedColumnName = "id")})
    @ManyToMany
    private List<UserEntity> usersWhoAreFriendsOfMe;
    @ToString.Exclude
    @ManyToMany(mappedBy = "usersWhoAreFriendsOfMe")
    private List<UserEntity> usersWhoChoseMeAsFriend;

}
