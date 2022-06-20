package com.example.project.api;

import com.example.project.database.entities.UserEntity;
import com.example.project.database.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/*
Autor: Florian Pronnegg, Jasmin Zach
Datum: 28.03.2022
Projektname: BikerBoost
 */

@RestController
@RequestMapping("/api/login")
public class LoginController {

    //Augaben der Klasse: dient zum Einloggen des Users

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public RedirectView post(@RequestParam("email") String email, @RequestParam("password") String password) {


        UserEntity user = userRepository.findByEmailAndPassword(email, password);
        if (user == null){
            return new RedirectView("/index.html");
        }else if(email.equals("") || password.equals("")){
            return new RedirectView("/index.html");
        }
        return new RedirectView("/user?user=" + user.getId());
    }

}
