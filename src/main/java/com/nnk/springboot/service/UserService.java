package com.nnk.springboot.service;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO convertToDTO(User user) {
        log.info("Converting User to UserDTO");
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setFullname(user.getFullName());
        dto.setRole(user.getRole());
        return dto;
    }

    public List<UserDTO> convertToDTO(List<User> users) {
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO getUserDTOById(int id){
        log.info("Fetching User with ID: {}", id);

        if (id <= 0) {
            log.error("Invalid ID.");
            throw new IllegalArgumentException("ID must be a positive integer.");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Bid list with id " + id + " not found"));
        return convertToDTO(user);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User saveUser(UserDTO userDTO) {
        log.info("Adding user " + userDTO);

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFullName(userDTO.getFullname());
        user.setRole(userDTO.getRole());
        try{
           User saveUser = userRepository.save(user);
           log.info("Saved user " + saveUser);
           return saveUser;
        }catch (DataAccessException e){
            log.error("Failed to save user", e);
            throw new EntitySaveException("Failed to create user.",e);
        }
    }

    public User updateUser(int id, UserDTO userDTO) {
        log.info("Updating user " + userDTO);

        if (userDTO == null) {
            log.error("UserDTO is null, cannot update.");
            throw new IllegalArgumentException("UserDTO cannot be null.");
        }

        User user = userRepository.findById(id).
                orElseThrow(()->
                 new EntityNotFoundException("user not found with id {}"+id));

        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFullName(userDTO.getFullname());
        user.setRole(userDTO.getRole());
        try{
            User saveUser = userRepository.save(user);
            log.info("Update user " + saveUser);
            return saveUser;
        }catch (DataAccessException e){
            log.error("Failed to update user", e);
            throw new EntitySaveException("Failed to update user.",e);
        }
    }

    public void deleteUser(int id) {
        log.info("Deleting user " + id);

        if (id <= 0) {
            log.error("Invalid ID: {}", id);
            throw new IllegalArgumentException("ID must be a positive integer.");
        }
        if (!userRepository.existsById(id)) {
            log.error("User with ID {} not found", id);
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
        try {
            userRepository.deleteById(id);
            log.info("User with ID {} deleted successfully", id);
        } catch (DataAccessException e) {
            log.error("Failed to delete user with ID {}", id, e);
            throw new EntityDeleteException("Failed to delete user with ID " + id, e);
        }

    }

    public User processOAuthPostLogin(OAuth2User oAuth2User) {
        String githubId = oAuth2User.getAttribute("id");
        User existingUser = userRepository.findByGithubId(githubId);

        if (existingUser == null) {
            // Si l'utilisateur n'existe pas, créez-le
            User newUser = new User();
            newUser.setGithubId(githubId);
            newUser.setUsername(oAuth2User.getAttribute("name"));
           // newUser.setEmail(oAuth2User.getAttribute("email"));

            return userRepository.save(newUser);
        }

        return existingUser;
    }

    public User registerUser(OAuth2User oAuth2User) {
        log.info("Registering user " + oAuth2User);
        String githubId = oAuth2User.getAttribute("id");
        User user = userRepository.findByGithubId(githubId);

        if (user == null) {
            // Si l'utilisateur n'existe pas, créez un compte
            user = new User();
            user.setGithubId(githubId);
            user.setUsername(oAuth2User.getAttribute("name"));
            user.setRole("ROLE_USER"); // Par défaut, assigner le rôle USER

            user = userRepository.save(user);
        }

        return user;
    }

    public User findByGithubId(String githubId) {
        return userRepository.findByGithubId(githubId);
    }

}
