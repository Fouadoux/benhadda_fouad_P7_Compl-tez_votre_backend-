package com.nnk.springboot.controllers;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * Displays the login page.
     *
     * @return the name of the login view (e.g., login.html in Thymeleaf templates)
     */
    @GetMapping("/login")
    public String login() {
        return "login"; // Renvoie la vue `login.html` pour le formulaire de connexion
    }

}
