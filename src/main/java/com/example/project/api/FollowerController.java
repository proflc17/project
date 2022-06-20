package com.example.project.api;

import com.example.project.database.entities.UserEntity;
import com.example.project.database.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.LinkedList;

/*
Autor: Florian Pronnegg, Jasmin Zach
Datum: 27.04.2022
Projektname: BikerBoost
 */

@Controller
@RequestMapping("/api/follower")
public class FollowerController {

    //Klasse für die Verwaltung von Freunden
    //Aufgaben der Klasse: hinzufügen und entfernen von Freunden

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String get(Model model, @RequestParam("user") Long userId, @RequestParam(value = "search", required = false) String search) {

        model.addAttribute("searchresult", userRepository.findUsersByName(search));

        model.addAttribute("friends", userRepository.findById(userId).get().getUsersWhoAreFriendsOfMe());

        model.addAttribute("user", userRepository.getById(userId));

        return "follower";
    }

    @PostMapping("{id}/addFriend/{fid}")
    public String addFriend(Model model, @PathVariable("id") Long id, @PathVariable("fid") Long fid) {
        UserEntity u = userRepository.findById(id).get();
        UserEntity f = userRepository.findById(fid).get();

            u.getUsersWhoAreFriendsOfMe().add(f);

            userRepository.saveAndFlush(f);

            model.addAttribute("searchresult", new LinkedList<UserEntity>());

            model.addAttribute("friends", userRepository.findById(id).get().getUsersWhoAreFriendsOfMe());

            model.addAttribute("user", userRepository.getById(id));


            return "follower";

    }

    @PostMapping("{id}/removeFriend/{fid}")
    public String removeFriend(Model model, @PathVariable("id") Long id, @PathVariable("fid") Long fid) {
        UserEntity u = userRepository.findById(id).get();
        UserEntity f = userRepository.findById(fid).get();

        u.getUsersWhoAreFriendsOfMe().remove(f);

        userRepository.saveAndFlush(f);

        model.addAttribute("searchresult", new LinkedList<UserEntity>());

        model.addAttribute("friends", userRepository.findById(id).get().getUsersWhoAreFriendsOfMe());

        model.addAttribute("user", userRepository.getById(id));

        return "follower";
    }


}

