package com.example.project.api;

import com.example.project.database.entities.UserEntity;
import com.example.project.database.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/api/register")
public class RegistrationController {

    //Aufgaben der Klasse: dient zum Registrieren des Users

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public RedirectView post(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("password2") String password2, @RequestParam("username") String username, @RequestParam("firstname") String firstname, @RequestParam("lastname")String lastname) {

        UserEntity name = userRepository.findUsersByUsername(username);

        if (!password.equals(password2)) {
            return new RedirectView("/register/registerScreen.html");
        } else if (name != null) {
            return new RedirectView("/register/registerScreen.html");
        } else if (email.isEmpty() || password.isEmpty() || lastname.isEmpty() || firstname.isEmpty() || username.isEmpty() || password2.isEmpty()) {
            return new RedirectView("/register/registerScreen.html");
        } else {
            UserEntity newUser = UserEntity.builder()
                    .firstname(firstname).lastname(lastname).username(username).email(email).password(password).build();

            userRepository.save(newUser);
            userRepository.flush();

            return new RedirectView("/index.html");
        }
    }
}
