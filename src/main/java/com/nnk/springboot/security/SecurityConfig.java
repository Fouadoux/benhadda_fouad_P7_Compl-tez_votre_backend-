package com.nnk.springboot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.web.SecurityFilterChain;


/**
 * Security configuration class for the application.
 * Configures authentication, authorization, and other security-related settings.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2SuccessHandler successHandler;
    private final EncoderConfig encoderConfig;

    /**
     * Constructs a new instance of {@link SecurityConfig}.
     *
     * @param userDetailsService the custom user details service for authentication
     * @param successHandler     the custom OAuth2 success handler
     * @param encoderConfig      the password encoder configuration
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomOAuth2SuccessHandler successHandler, EncoderConfig encoderConfig) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
        this.encoderConfig = encoderConfig;
    }

    /**
     * Configures the security filter chain.
     *
     * @param http the {@link HttpSecurity} object for security configuration
     * @return the security filter chain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection if not needed
                .csrf(AbstractHttpConfigurer::disable)

                // (2) Configure URL-based authorization
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login","/oauth2/**","/register").permitAll()
                        .anyRequest().authenticated()
                )

                // (3) Configure form login
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/bidList/list", true)
                        .permitAll()
                )

                // (4) Configure OAuth2 login
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // Ajoute une option pour se connecter avec GitHub
                        .successHandler(successHandler)
                )
                // Configure HTTP Basic authentication (optional)
                .httpBasic(Customizer.withDefaults())

                // (5) Configure logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )

                // (6) Handle access denied exceptions by redirecting to the 403 page
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/403")
                );

        return http.build();
    }

    /**
     * Configures the {@link AuthenticationManager}.
     *
     * @param authenticationConfiguration the authentication configuration
     * @return the authentication manager
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures the authentication provider using {@link DaoAuthenticationProvider}.
     *
     * @return the authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoderConfig.passwordEncoder());
        return provider;
    }

}
