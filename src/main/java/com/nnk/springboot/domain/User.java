package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails {

    /*@Transient
    private Collection<? extends GrantedAuthority> authorities;*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Correspond à AUTO_INCREMENT dans MySQL
    private int id;

    @Column(name = "username", length = 125)
    private String username;

    @Column(name = "password", length = 125)
    private String password;

    @Column(name = "fullname", length = 125)
    private String fullName;

    @Column(name = "role", length = 125)
    private String role;

    private String githubId; // ID unique de GitHub

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role)); // this.role doit inclure "ROLE_ADMIN"
    }
}
