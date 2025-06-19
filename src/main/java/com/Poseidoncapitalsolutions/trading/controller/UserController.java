package com.poseidoncapitalsolutions.trading.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.poseidoncapitalsolutions.trading.dto.UserDTO;
import com.poseidoncapitalsolutions.trading.mapper.UserMapper;
import com.poseidoncapitalsolutions.trading.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller responsible for handling user-related operations in the system.
 * Provides endpoints for viewing, adding, updating, and deleting users.
 */
@Controller
@Tag(name = "User Management", description = "API for managing users in the system")
public class UserController {

    private UserService userService;
    private UserMapper userMapper;

    /**
     * Constructs a UserController with the given service and mapper.
     * 
     * @param userService The service to interact with User data.
     * @param userMapper  The mapper to convert User entities to DTOs.
     */
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Displays a list of all users in the system.
     * 
     * @param model The model to add the list of users to.
     * @return The view name for the users list page.
     */
    @Operation(summary = "Get all users", description = "Returns a list of all users in the system")
    @GetMapping("/user/list")
    public String home(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    /**
     * Displays the form for adding a new user.
     * 
     * @param model The model to add a new empty UserDTO to.
     * @return The view name for the add user form.
     */
    @Operation(summary = "Show add user form", description = "Displays form for adding a new user")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/add")
    public String addUser(Model model) {
        model.addAttribute("newUser", new UserDTO());
        return "user/add";
    }

    /**
     * Validates and creates a new user with the provided details.
     * 
     * @param user   The new UserDTO to be created.
     * @param result The result of validating the user input.
     * @param model  The model to pass attributes to the view.
     * @return A redirect to the user list page if the user is successfully created,
     *         or the form page if validation fails.
     */
    @Operation(summary = "Create new user", description = "Creates a new user with the provided details")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/user/validate")
    public String validate(
            @Parameter(description = "User data to be created", required = true) @ModelAttribute("newUser") @Valid UserDTO user,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("newUser", user);
            return "user/add";
        }

        userService.save(userMapper.toEntity(user));
        return "redirect:/user/list";
    }

    /**
     * Displays the form to update an existing user's details.
     * 
     * @param id    The ID of the user to be updated.
     * @param model The model to add the current user data to.
     * @return The view name for the update user form.
     */
    @Operation(summary = "Show update user form", description = "Displays form for updating an existing user")
    @PreAuthorize("hasAuthority('ADMIN') or #id == authentication.principal.id")
    @GetMapping("/user/update/{id}")
    public String showUpdateForm(
            @Parameter(description = "ID of the user to be updated", required = true) @PathVariable("id") int id,
            Model model) {
        model.addAttribute("updatedUser", userMapper.toDto(userService.findById(id)));
        return "user/update";
    }

    /**
     * Validates and updates an existing user with the provided details.
     * 
     * @param id     The ID of the user to be updated.
     * @param user   The updated UserDTO with new details.
     * @param result The result of validating the updated user input.
     * @param model  The model to pass attributes to the view.
     * @return A redirect to the user list page if the user is successfully updated,
     *         or the form page if validation fails.
     */
    @Operation(summary = "Update existing user", description = "Updates an existing user with the provided details")
    @PreAuthorize("hasAuthority('ADMIN') or #id == authentication.principal.id")
    @PostMapping("/user/update/{id}")
    public String updateUser(
            @Parameter(description = "ID of the user to be updated", required = true) @PathVariable("id") int id,
            @Parameter(description = "Updated user data", required = true) @ModelAttribute("updatedUser") @Valid UserDTO user,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("updatedUser", user);
            return "user/update";
        }

        userService.update(user);
        return "redirect:/user/list";
    }

    /**
     * Deletes a user from the system by their ID.
     * 
     * @param id    The ID of the user to be deleted.
     * @param model The model to pass attributes to the view.
     * @return A redirect to the user list page after the user is deleted.
     */
    @Operation(summary = "Delete user", description = "Deletes a user from the system")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/delete/{id}")
    public String deleteUser(
            @Parameter(description = "ID of the user to be deleted", required = true) @PathVariable("id") int id,
            Model model) {
        userService.delete(userService.findById(id));
        return "redirect:/user/list";
    }
}
