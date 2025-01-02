package com.nnk.springboot.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {

    private int id;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

    @NotBlank(message = "FullName is mandatory")
    private String fullname;

    @NotBlank(message = "Role is mandatory")
    private String role;

    private String githubId;
}
