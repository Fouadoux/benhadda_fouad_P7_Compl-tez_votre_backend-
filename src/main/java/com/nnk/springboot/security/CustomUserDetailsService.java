package com.nnk.springboot.security;

import org.springframework.security.core.userdetails.User;
import com.nnk.springboot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


/*    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(()->
                new UsernameNotFoundException("User not found"));

    }*/

  /*  @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        com.nnk.springboot.domain.User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userName));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().replace("ROLE_", "")) // Utilise le rôle défini dans votre base
                .build();
    }*/

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Récupérer l'utilisateur depuis la base de données
        com.nnk.springboot.domain.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Remplir les autorités dynamiquement
       // user.setAuthorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));

        return user; // Retourne l'utilisateur avec ses autorités
    }

}
