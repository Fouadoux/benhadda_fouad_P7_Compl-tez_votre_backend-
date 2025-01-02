package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
public class UserController {

    private final UserService userService;

    /**
     * Constructs a new instance of {@link UserController}.
     *
     * @param userService the service for managing users
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the list of all users.
     * Accessible only to users with the `ROLE_ADMIN`.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the user list
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("/user/list")
    public String home(Model model)
    {
        List<User> users = userService.getAllUser();
        List<UserDTO> userDTOs =userService.convertToDTO(users);
        model.addAttribute("users", userDTOs);
        return "user/list";
    }

    /**
     * Displays the form to add a new user.
     * Accessible only to users with the `ROLE_ADMIN`.
     *
     * @param model the model to pass attributes to the view
     * @return the view name for the add user form
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/add")
    public String addUser(Model model) {
        model.addAttribute("user", new UserDTO());
        return "user/add";
    }

    /**
     * Validates and saves a new user.
     * Accessible only to users with the `ROLE_ADMIN`.
     *
     * @param user               the user data transfer object to save
     * @param result             the binding result for validation errors
     * @param model              the model to pass attributes to the view
     * @param redirectAttributes the attributes to pass on redirection
     * @return the view name or redirect URL
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/user/validate")
    public String validate(@Valid @ModelAttribute UserDTO user, BindingResult result,
                           Model model, RedirectAttributes redirectAttributes) {

        if(result.hasErrors()){
            model.addAttribute("user", user);
            return "user/add";
        }
        try{
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User added successfully");
            return "redirect:/user/list";
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            return "user/add";
        }
    }

    /**
     * Displays the form to update an existing user.
     * Accessible only to users with the `ROLE_ADMIN`.
     *
     * @param id    the ID of the user to update
     * @param model the model to pass attributes to the view
     * @return the view name for the update user form
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        UserDTO userDTO = userService.getUserDTOById(id);
        model.addAttribute("user", userDTO);
        return "user/update";
    }

    /**
     * Validates and updates an existing user.
     * Accessible only to users with the `ROLE_ADMIN`.
     *
     * @param id                 the ID of the user to update
     * @param user               the user data transfer object with updated details
     * @param result             the binding result for validation errors
     * @param model              the model to pass attributes to the view
     * @param redirectAttributes the attributes to pass on redirection
     * @return the view name or redirect URL
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid @ModelAttribute UserDTO user,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("id", id);
            return "user/update";
        }
        try {
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
            return "redirect:/user/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            return "user/update";

        }
    }

    /**
     * Deletes a user by its ID.
     * Accessible only to users with the `ROLE_ADMIN`.
     *
     * @param id                 the ID of the user to delete
     * @param redirectAttributes the attributes to pass on redirection
     * @return the redirect URL for the user list
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
       /* User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        model.addAttribute("users", userRepository.findAll());*/

        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        return "redirect:/user/list";
    }
}
