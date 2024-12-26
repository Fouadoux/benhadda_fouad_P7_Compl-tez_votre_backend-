package com.nnk.springboot.security;

import com.nnk.springboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public CustomOAuth2SuccessHandler(UserService userService) {
        this.userService = userService;
        System.out.println("CustomOAuth2SuccessHandler initialized");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("onAuthenticationSuccess");
        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            // Enregistrez l'utilisateur dans votre base de données
            log.info("oAuth2User: {}", oAuth2User);
            userService.registerUser(oAuth2User);
        }

        // Redirigez vers la page d'accueil après succès
        response.sendRedirect("/bidList/list");
        log.info("onAuthenticationSuccess");
    }

}
