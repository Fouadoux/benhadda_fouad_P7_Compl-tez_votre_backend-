package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("/user/list")
    public String home(Model model)
    {
        List<User> users = userService.getAllUser();
        List<UserDTO> userDTOs =userService.convertToDTO(users);
        model.addAttribute("users", userDTOs);
        return "user/list";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/add")
    public String addUser(Model model) {
        model.addAttribute("user", new UserDTO());
        return "user/add";
    }
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        /*User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setPassword("");*/
        UserDTO userDTO = userService.getUserDTOById(id);
        model.addAttribute("user", userDTO);
        return "user/update";
    }
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
        /*if (result.hasErrors()) {
            return "user/update";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setId(id);
        userRepository.save(user);
        model.addAttribute("users", userRepository.findAll());
        return "redirect:/user/list";*/

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
