package com.example.project.api;

import com.example.project.database.entities.UserEntity;
import com.example.project.database.repos.UserRepository;
import org.apache.catalina.User;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

/*
Autor: Florian Pronnegg, Jasmin Zach
Datum: 27.04.2022
Projektname: BikerBoost
 */

@RestController
@RequestMapping("/api/user")
public class UserController {

    //Aufgaben der Klasse: User suchen, Freunde hinzufügen

    @Autowired
    public UserRepository userRepository;

    @GetMapping(value = "/usersbyname/{name}")
    public List<UserEntity> getUsersByName(@PathVariable("name")String name, Model model){
        List<UserEntity> users = userRepository.findUsersByName(name);
        System.out.println(users);
        model.addAttribute("searchresult", users);

        return users;
    }

    @PostMapping(value = "{id}/addFriend/{fid}")
    public RedirectView addFriend(@PathVariable("id") Long id, @PathVariable("fid") Long fid) {
        UserEntity u = userRepository.findById(id).get();
        UserEntity f = userRepository.findById(fid).get();

        u.getUsersWhoAreFriendsOfMe().add(f);

        userRepository.saveAndFlush(f);

        return new RedirectView("/follower/" + id);
    }

}
