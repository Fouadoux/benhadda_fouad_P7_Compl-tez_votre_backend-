package com.nnk.springboot.service;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldConvertUserToUserDTO_WhenConvertToDTOCalled() {
        // GIVEN
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("testpass");
        user.setFullName("Test User");
        user.setRole("ROLE_USER");

        // WHEN
        UserDTO dto = userService.convertToDTO(user);

        // THEN
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getPassword(), dto.getPassword());
        assertEquals(user.getFullName(), dto.getFullname());
        assertEquals(user.getRole(), dto.getRole());
    }

    @Test
    void shouldConvertListOfUsersToListOfUserDTOs_WhenConvertToDTOListCalled() {
        // GIVEN
        User u1 = new User();
        u1.setId(1);
        u1.setUsername("user1");
        u1.setPassword("pass1");
        u1.setFullName("User One");
        u1.setRole("ROLE_USER");

        User u2 = new User();
        u2.setId(2);
        u2.setUsername("user2");
        u2.setPassword("pass2");
        u2.setFullName("User Two");
        u2.setRole("ROLE_ADMIN");

        List<User> users = List.of(u1, u2);

        // WHEN
        List<UserDTO> dtos = userService.convertToDTO(users);

        // THEN
        assertNotNull(dtos);
        assertEquals(2, dtos.size());

        // Contrôles sur le premier utilisateur
        assertEquals(u1.getId(), dtos.get(0).getId());
        assertEquals(u1.getUsername(), dtos.get(0).getUsername());
        assertEquals(u1.getPassword(), dtos.get(0).getPassword());
        assertEquals(u1.getFullName(), dtos.get(0).getFullname());
        assertEquals(u1.getRole(), dtos.get(0).getRole());

        // Contrôles sur le deuxième utilisateur
        assertEquals(u2.getId(), dtos.get(1).getId());
        assertEquals(u2.getUsername(), dtos.get(1).getUsername());
        assertEquals(u2.getPassword(), dtos.get(1).getPassword());
        assertEquals(u2.getFullName(), dtos.get(1).getFullname());
        assertEquals(u2.getRole(), dtos.get(1).getRole());
    }

    @Test
    void shouldReturnUserDTO_WhenValidIdProvided() {
        // GIVEN
        int validId = 5;
        User mockUser = new User();
        mockUser.setId(validId);
        mockUser.setUsername("tester");
        mockUser.setPassword("secret");
        mockUser.setFullName("Test Name");
        mockUser.setRole("ROLE_USER");

        when(userRepository.findById(validId)).thenReturn(Optional.of(mockUser));

        // WHEN
        UserDTO result = userService.getUserDTOById(validId);

        // THEN
        assertNotNull(result);
        assertEquals(validId, result.getId());
        assertEquals("tester", result.getUsername());
        verify(userRepository).findById(validId);
    }

    @Test
    void shouldThrowIllegalArgumentException_WhenInvalidIdProvidedForGetUserDTO() {
        // GIVEN
        int invalidId = 0;

        // WHEN + THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserDTOById(invalidId);
        });
        assertTrue(ex.getMessage().contains("ID must be a positive integer."));
        verify(userRepository, never()).findById(anyInt());
    }

    @Test
    void shouldThrowEntityNotFoundException_WhenGettingNonexistentUserDTOById() {
        // GIVEN
        int nonExistentId = 999;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // WHEN + THEN
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserDTOById(nonExistentId);
        });
        assertTrue(ex.getMessage().contains("not found"));
        verify(userRepository).findById(nonExistentId);
    }

    @Test
    void shouldReturnAllUsers_WhenGetAllUserCalled() {
        // GIVEN
        User u1 = new User();
        u1.setId(1);
        User u2 = new User();
        u2.setId(2);

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        // WHEN
        List<User> allUsers = userService.getAllUser();

        // THEN
        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
        verify(userRepository).findAll();
    }

    @Test
    void shouldSaveUserSuccessfully_WhenValidUserDTOProvided() {
        // GIVEN
        UserDTO dto = new UserDTO();
        dto.setUsername("newuser");
        dto.setPassword("newpass");
        dto.setFullname("New Fullname");
        dto.setRole("ROLE_ADMIN");

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("newuser");
        mockUser.setPassword("encodedPass");
        mockUser.setFullName("New Fullname");
        mockUser.setRole("ROLE_ADMIN");

        // Simulation de l'encodage du mot de passe
        when(passwordEncoder.encode("newpass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // WHEN
        User saved = userService.saveUser(dto);

        // THEN
        assertNotNull(saved);
        assertEquals(1, saved.getId());
        assertEquals("newuser", saved.getUsername());
        assertEquals("encodedPass", saved.getPassword());
        verify(passwordEncoder).encode("newpass");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowEntitySaveException_WhenSaveUserFailsDueToDataAccessException() {
        // GIVEN
        UserDTO dto = new UserDTO();
        dto.setUsername("failuser");
        dto.setPassword("failpass");
        dto.setFullname("Fail Fullname");
        dto.setRole("ROLE_ADMIN");

        when(passwordEncoder.encode("failpass")).thenReturn("encodedFailPass");
        // Simuler une exception lors de la sauvegarde
        doThrow(new DataAccessException("DB error") {}).when(userRepository).save(any(User.class));

        // WHEN + THEN
        EntitySaveException ex = assertThrows(EntitySaveException.class, () -> {
            userService.saveUser(dto);
        });
        assertEquals("Failed to create user.", ex.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldUpdateUserSuccessfully_WhenValidUserDTOProvided() {
        // GIVEN
        int userId = 10;
        UserDTO dto = new UserDTO();
        dto.setUsername("updatedName");
        dto.setPassword("updatedPass");
        dto.setFullname("Updated FullName");
        dto.setRole("ROLE_USER");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldName");
        existingUser.setPassword("oldPass");
        existingUser.setFullName("Old FullName");
        existingUser.setRole("ROLE_ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("updatedPass")).thenReturn("encodedUpdatedPass");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // WHEN
        User updated = userService.updateUser(userId, dto);

        // THEN
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode("updatedPass");
        verify(userRepository).save(existingUser);
        assertEquals("updatedName", existingUser.getUsername());
        assertEquals("encodedUpdatedPass", existingUser.getPassword());
        assertEquals("Updated FullName", existingUser.getFullName());
        assertEquals("ROLE_USER", existingUser.getRole());
        assertNotNull(updated);
    }

    @Test
    void shouldThrowIllegalArgumentException_WhenNullUserDTOProvidedForUpdate() {
        // GIVEN
        int userId = 1;
        UserDTO dto = null;

        // WHEN + THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, dto);
        });
        assertTrue(ex.getMessage().contains("cannot be null"));
        verify(userRepository, never()).findById(anyInt());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowEntityNotFoundException_WhenUpdatingNonexistentUser() {
        // GIVEN
        int userId = 999;
        UserDTO dto = new UserDTO();
        dto.setUsername("someName");
        dto.setPassword("somePass");
        dto.setFullname("someFullName");
        dto.setRole("ROLE_USER");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // WHEN + THEN
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUser(userId, dto);
        });
        assertTrue(ex.getMessage().contains("user not found with id"));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowEntitySaveException_WhenUpdateUserFailsDueToDataAccessException() {
        // GIVEN
        int userId = 2;
        UserDTO dto = new UserDTO();
        dto.setUsername("failName");
        dto.setPassword("failPass");
        dto.setFullname("failFullName");
        dto.setRole("ROLE_ADMIN");

        User existingUser = new User();
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("failPass")).thenReturn("encodedFailPass");
        doThrow(new DataAccessException("DB error") {}).when(userRepository).save(existingUser);

        // WHEN + THEN
        EntitySaveException ex = assertThrows(EntitySaveException.class, () -> {
            userService.updateUser(userId, dto);
        });
        assertEquals("Failed to update user.", ex.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

    @Test
    void shouldDeleteUserSuccessfully_WhenValidIdProvided() {
        // GIVEN
        int userId = 3;
        when(userRepository.existsById(userId)).thenReturn(true);

        // WHEN
        userService.deleteUser(userId);

        // THEN
        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void shouldThrowIllegalArgumentException_WhenInvalidIdProvidedForDeleteUser() {
        // GIVEN
        int invalidId = 0;

        // WHEN + THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(invalidId);
        });
        assertTrue(ex.getMessage().contains("must be a positive integer."));
        verify(userRepository, never()).existsById(anyInt());
        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldThrowEntityNotFoundException_WhenDeletingNonexistentUser() {
        // GIVEN
        int userId = 999;
        when(userRepository.existsById(userId)).thenReturn(false);

        // WHEN + THEN
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });
        assertTrue(ex.getMessage().contains("User not found with ID"));
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldThrowEntityDeleteException_WhenDeleteUserFailsDueToDataAccessException() {
        // GIVEN
        int userId = 4;
        when(userRepository.existsById(userId)).thenReturn(true);
        doThrow(new DataAccessException("DB error") {}).when(userRepository).deleteById(userId);

        // WHEN + THEN
        EntityDeleteException ex = assertThrows(EntityDeleteException.class, () -> {
            userService.deleteUser(userId);
        });
        assertTrue(ex.getMessage().contains("Failed to delete user with ID"));
        verify(userRepository).deleteById(userId);
    }
}
