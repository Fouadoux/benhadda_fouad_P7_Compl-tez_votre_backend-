package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // Renvoie la vue register.html
    }


    /**
     * Page d'enregistrement après connexion via GitHub.
     */
  /*  @GetMapping
    public String register(Authentication authentication, Model model) {
        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            // Récupérer les informations depuis GitHub

            User user = userService.registerUser(oAuth2User);

            return "home"; // Vue welcome.html
        }

        // Si l'utilisateur n'est pas authentifié via GitHub, rediriger vers une erreur
        return "redirect:/403";
    }*/
}
