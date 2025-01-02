package com.nnk.springboot.security;

import com.nnk.springboot.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * Custom implementation of {@link OAuth2User} that integrates application-specific {@link User} data
 * with OAuth2 attributes.
 */

public class CustomOAuth2User implements OAuth2User {

    private final User user; // L'utilisateur principal (implémente UserDetails)
    private final Map<String, Object> attributes; // Attributs OAuth2

    /**
     * Constructs a new instance of {@link CustomOAuth2User}.
     *
     * @param user       the application user
     * @param attributes the OAuth2 attributes
     */
    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // Exposez les méthodes spécifiques de User
    public int getId() {
        return user.getId();
    }

    public String getGithubId() {
        return user.getGithubId();
    }

    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Retrieves the attributes provided by the OAuth2 provider.
     *
     * @return a map of OAuth2 attributes
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Retrieves the authorities granted to the user.
     *
     * @return a collection of {@link GrantedAuthority} instances
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    /**
     * Retrieves the principal name of the user.
     *
     * @return the username, used as the principal name
     */
    @Override
    public String getName() {
        return user.getUsername();
    }
}
