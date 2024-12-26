package com.nnk.springboot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Renvoie la vue `login.html` pour le formulaire de connexion
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String name = oAuth2User.getAttribute("name"); // Nom de l'utilisateur GitHub
            String email = oAuth2User.getAttribute("email"); // Email de l'utilisateur GitHub (peut être null si non public)
            model.addAttribute("name", name);
            model.addAttribute("email", email);
        } else {
            // Ajouter des informations pour les connexions classiques si nécessaire
            model.addAttribute("name", authentication.getName());
        }
        return "home"; // Renvoie la vue `home.html`
    }

}
