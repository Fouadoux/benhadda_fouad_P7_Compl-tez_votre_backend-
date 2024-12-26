package com.nnk.springboot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2SuccessHandler successHandler) throws Exception {
        http
                // (1) Désactivation CSRF si nécessaire
                .csrf(AbstractHttpConfigurer::disable)

                // (2) Configuration des autorisations par URL
                .authorizeHttpRequests(auth -> auth
                        // Autorise l'accès à la page de login et à la page 403 sans être authentifié
                        .requestMatchers("/login","/oauth2/**","/register").permitAll()
                        // Tout le reste nécessite une authentification
                        .anyRequest().authenticated()
                )

                // (3) Configuration du formulaire de login
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/bidList/list", true)
                         // Gestionnaire personnalisé pour enregistrer les utilisateurs GitHub
                        .permitAll()
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/register") // Ajoute une option pour se connecter avec GitHub
                        .successHandler(successHandler)
                        .defaultSuccessUrl("/bidList/list", true)
                )

                // (4) HTTP Basic (optionnel, selon tes besoins)
                .httpBasic(Customizer.withDefaults())

                // (5) Configuration du logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )

                // (6) Gestion des exceptions : rediriger vers la page 403 en cas de refus d'accès
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/403")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


}
