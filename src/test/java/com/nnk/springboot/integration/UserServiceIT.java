package com.nnk.springboot.integration;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;

import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void saveUser_ShouldSaveUserSuccessfully() throws EntitySaveException {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setPassword("testPassword");
        userDTO.setFullname("Test User");
        userDTO.setRole("ROLE_USER");

        // Act
        User savedUser = userService.saveUser(userDTO);

        // Assert
        assertNotNull(savedUser.getId());
        assertEquals("testUser", savedUser.getUsername());
        assertTrue(passwordEncoder.matches("testPassword", savedUser.getPassword()));
        assertEquals("Test User", savedUser.getFullName());
        assertEquals("ROLE_USER", savedUser.getRole());
    }

    @Test
    void saveUser_ShouldThrowExceptionWhenDataInvalid() {
        // Arrange
        UserDTO userDTO = new UserDTO(); // Missing required fields

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(userDTO));
    }

    @Test
    void getAllUser_ShouldReturnAllSavedUsers() throws EntitySaveException {
        // Arrange
        UserDTO user1 = new UserDTO();
        user1.setUsername("user1");
        user1.setPassword("password1");
        user1.setFullname("User One");
        user1.setRole("ROLE_USER");
        userService.saveUser(user1);

        UserDTO user2 = new UserDTO();
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setFullname("User Two");
        user2.setRole("ROLE_ADMIN");
        userService.saveUser(user2);

        // Act
        List<User> allUsers = userService.getAllUser();

        // Assert
        assertEquals(2, allUsers.size());
    }

    @Test
    void getUserDTOById_ShouldReturnUserDTO() throws EntityNotFoundException, EntitySaveException {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setPassword("testPassword");
        userDTO.setFullname("Test User");
        userDTO.setRole("ROLE_USER");
        User savedUser = userService.saveUser(userDTO);

        // Act
        UserDTO fetchedUser = userService.getUserDTOById(savedUser.getId());

        // Assert
        assertNotNull(fetchedUser);
        assertEquals(savedUser.getId(), fetchedUser.getId());
    }

    @Test
    void getUserDTOById_ShouldThrowExceptionWhenUserNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getUserDTOById(999));
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() throws EntitySaveException, EntityNotFoundException {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setPassword("testPassword");
        userDTO.setFullname("Test User");
        userDTO.setRole("ROLE_USER");
        User savedUser = userService.saveUser(userDTO);

        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setUsername("updatedUser");
        updatedUserDTO.setPassword("updatedPassword");
        updatedUserDTO.setFullname("Updated User");
        updatedUserDTO.setRole("ROLE_ADMIN");

        // Act
        User updatedUser = userService.updateUser(savedUser.getId(), updatedUserDTO);

        // Assert
        assertEquals("updatedUser", updatedUser.getUsername());
        assertTrue(passwordEncoder.matches("updatedPassword", updatedUser.getPassword()));
        assertEquals("Updated User", updatedUser.getFullName());
        assertEquals("ROLE_ADMIN", updatedUser.getRole());
    }

    @Test
    void updateUser_ShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setUsername("updatedUser");
        updatedUserDTO.setPassword("updatedPassword");
        updatedUserDTO.setFullname("Updated User");
        updatedUserDTO.setRole("ROLE_ADMIN");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(999, updatedUserDTO));
    }

    @Test
    void deleteUser_ShouldDeleteUserSuccessfully() throws EntitySaveException {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setPassword("testPassword");
        userDTO.setFullname("Test User");
        userDTO.setRole("ROLE_USER");
        User savedUser = userService.saveUser(userDTO);

        // Act
        userService.deleteUser(savedUser.getId());
        boolean exists = userRepository.existsById(savedUser.getId());

        // Assert
        assertFalse(exists);
    }

    @Test
    void deleteUser_ShouldThrowExceptionWhenUserNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(999));
    }
}
