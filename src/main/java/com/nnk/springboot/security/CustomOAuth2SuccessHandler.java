package com.nnk.springboot.security;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom implementation of {@link AuthenticationSuccessHandler} for handling successful OAuth2 logins.
 * It ensures that authenticated users are registered in the database and updates the SecurityContext.
 */

@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    /**
     * Constructs a new instance of {@link CustomOAuth2SuccessHandler}.
     *
     * @param userService the service for managing users
     */
    public CustomOAuth2SuccessHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles successful OAuth2 authentication.
     *
     * @param request        the HTTP request
     * @param response       the HTTP response
     * @param authentication the authentication object
     * @throws IOException if an input or output error occurs while handling the request
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2Token) {
            OAuth2User oAuth2User = oAuth2Token.getPrincipal();

            // Register or update the user in the database
            User user = userService.registerUser(oAuth2User);

            // Create a CustomOAuth2User to combine User and OAuth2 data
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(user, oAuth2User.getAttributes());

            // Create a new OAuth2AuthenticationToken with the custom user details
            OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                    customOAuth2User,
                    customOAuth2User.getAuthorities(),
                    oAuth2Token.getAuthorizedClientRegistrationId()
            );

            // Update the SecurityContext with the new authentication
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        // Redirect the user after successful authentication
        response.sendRedirect("/bidList/list");
    }


}
