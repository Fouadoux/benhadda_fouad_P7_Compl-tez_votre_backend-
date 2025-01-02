package com.nnk.springboot.service;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.*;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new instance of {@link UserService}.
     *
     * @param userRepository  the repository for accessing user data
     * @param passwordEncoder the encoder for hashing passwords
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Converts a {@link User} entity to a {@link UserDTO}.
     *
     * @param user the user entity to convert
     * @return the converted {@link UserDTO}
     */
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

    /**
     * Converts a list of {@link User} entities to a list of {@link UserDTO}s.
     *
     * @param users the list of user entities to convert
     * @return the list of converted {@link UserDTO}s
     */
    public List<UserDTO> convertToDTO(List<User> users) {
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves a {@link UserDTO} by its ID.
     *
     * @param id the ID of the user
     * @return the retrieved {@link UserDTO}
     * @throws IllegalArgumentException  if the ID is invalid
     * @throws EntityNotFoundException   if no user is found with the given ID
     */
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

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    /**
     * Saves a new user based on a {@link UserDTO}.
     *
     * @param userDTO the data transfer object containing user details
     * @return the saved {@link User} entity
     * @throws EntitySaveException if saving the user fails
     */
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

    /**
     * Updates an existing user with new details.
     *
     * @param id      the ID of the user to update
     * @param userDTO the data transfer object containing updated user details
     * @return the updated {@link User} entity
     * @throws IllegalArgumentException  if the {@link UserDTO} is null
     * @throws EntityNotFoundException   if no user is found with the given ID
     * @throws EntitySaveException       if updating the user fails
     */
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

    /**
     * Deletes a user by its ID.
     *
     * @param id the ID of the user to delete
     * @throws IllegalArgumentException   if the ID is invalid
     * @throws EntityNotFoundException    if no user is found with the given ID
     * @throws EntityDeleteException      if deleting the user fails
     */
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

    /**
     * Registers a user using OAuth2 authentication.
     *
     * @param oAuth2User the authenticated OAuth2 user
     * @return the registered {@link User} entity
     */
    public User registerUser(OAuth2User oAuth2User) {
        log.info("Registering user: " + oAuth2User);

        // Conversion explicite de l'attribut 'id'
        Object idObject = oAuth2User.getAttribute("id");
        String githubId = (idObject instanceof String) ? (String) idObject : String.valueOf(idObject);

        log.info("GitHub ID: " + githubId);

        // Vérifiez si l'utilisateur existe déjà dans la base de données
        User user = userRepository.findByGithubId(githubId);
        if (user == null) {
            log.info("User not found. Creating a new user...");

            // Création de l'utilisateur
            user = new User();
            user.setGithubId(githubId);
            user.setUsername(oAuth2User.getAttribute("login"));
            user.setRole("ROLE_USER");
            user.setFullName(oAuth2User.getAttribute("login"));

            user = userRepository.save(user);
            log.info("User saved successfully: " + user);
        } else {
            log.info("User already exists: " + user);
        }

        return user;
    }

}
