package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void home_ShouldReturnUserListViewWithUsers() {
        // Arrange
        List<User> users = Arrays.asList(new User(), new User());
        List<UserDTO> userDTOs = Arrays.asList(new UserDTO(), new UserDTO());
        when(userService.getAllUser()).thenReturn(users);
        when(userService.convertToDTO(users)).thenReturn(userDTOs);

        // Act
        String viewName = userController.home(model);

        // Assert
        assertEquals("user/list", viewName);
        verify(model).addAttribute("users", userDTOs);
    }

    @Test
    void addUser_ShouldReturnAddUserView() {
        // Act
        String viewName = userController.addUser(model);

        // Assert
        assertEquals("user/add", viewName);
        verify(model).addAttribute(eq("user"), any(UserDTO.class));
    }

    @Test
    void validate_ShouldSaveUserAndRedirectToUserList() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = userController.validate(userDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/user/list", viewName);
        verify(userService).saveUser(userDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "User added successfully");
    }

    @Test
    void validate_ShouldReturnAddUserViewWhenValidationFails() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = userController.validate(userDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("user/add", viewName);
        verify(model).addAttribute("user", userDTO);
    }

    @Test
    void validate_ShouldReturnAddUserViewWhenSaveFails() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Error saving user")).when(userService).saveUser(userDTO);

        // Act
        String viewName = userController.validate(userDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("user/add", viewName);
        verify(model).addAttribute("user", userDTO);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Error saving user");
    }

    @Test
    void showUpdateForm_ShouldReturnUpdateUserView() {
        // Arrange
        int id = 1;
        UserDTO userDTO = new UserDTO();
        when(userService.getUserDTOById(id)).thenReturn(userDTO);

        // Act
        String viewName = userController.showUpdateForm(id, model);

        // Assert
        assertEquals("user/update", viewName);
        verify(model).addAttribute("user", userDTO);
    }

    @Test
    void updateUser_ShouldUpdateUserAndRedirectToUserList() {
        // Arrange
        int id = 1;
        UserDTO userDTO = new UserDTO();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = userController.updateUser(id, userDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/user/list", viewName);
        verify(userService).updateUser(id, userDTO);
        verify(redirectAttributes).addFlashAttribute("successMessage", "User updated successfully");
    }

    @Test
    void updateUser_ShouldReturnUpdateUserViewWhenValidationFails() {
        // Arrange
        int id = 1;
        UserDTO userDTO = new UserDTO();
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = userController.updateUser(id, userDTO, bindingResult, model, redirectAttributes);

        // Assert
        assertEquals("user/update", viewName);
        verify(model).addAttribute("user", userDTO);
        verify(model).addAttribute("id", id);
    }

    @Test
    void deleteUser_ShouldDeleteUserAndRedirectToUserList() {
        // Arrange
        int id = 1;

        // Act
        String viewName = userController.deleteUser(id, redirectAttributes);

        // Assert
        assertEquals("redirect:/user/list", viewName);
        verify(userService).deleteUser(id);
        verify(redirectAttributes).addFlashAttribute("successMessage", "User deleted successfully");
    }
}
