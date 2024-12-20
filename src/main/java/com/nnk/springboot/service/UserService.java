package com.nnk.springboot.service;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Bid list with id " + id + " not found"));
        return convertToDTO(user);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public void addUser(UserDTO userDTO) {
        log.info("Adding user " + userDTO);
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFullName(userDTO.getFullname());
        user.setRole(userDTO.getRole());
        userRepository.save(user);

    }

    public void updateUser(int id, UserDTO userDTO) {
        log.info("Updating user " + userDTO);

        User user = userRepository.findById(id).
                orElseThrow(()->
                 new EntityNotFoundException("user not found with id {}"+id));

        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFullName(userDTO.getFullname());
        user.setRole(userDTO.getRole());
        userRepository.save(user);

    }

    public void deleteUser(int id) {
        log.info("Deleting user " + id);
        userRepository.deleteById(id);
    }

}
