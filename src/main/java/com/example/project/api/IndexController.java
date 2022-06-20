package com.example.project.api;

import com.example.project.database.entities.UserEntity;
import com.example.project.database.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
Autor: Florian Pronnegg, Jasmin Zach
Datum: 28.03.2022
Projektname: BikerBoost
 */

@Controller
@RequestMapping("/")
public class IndexController {

    //Aufgaben der Klasse: Index des Users bekommen, damit man damit arbeiten kann

    @Autowired
    UserRepository userRepository;

    @GetMapping
    @RequestMapping("user")
    public String get(Model model, @RequestParam("user") Long userId) {

        Optional<UserEntity> userOpt = userRepository.findById(userId);

        UserEntity user = userOpt.get();


        model.addAttribute("user", user);

       List<Long> followers = new ArrayList<>(){{
            this.add(12L);
            this.add(13L);
        }};

        model.addAttribute("followers", followers);

        return "user";
    }

}
