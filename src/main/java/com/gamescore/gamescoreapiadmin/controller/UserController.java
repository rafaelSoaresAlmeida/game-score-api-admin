package com.gamescore.gamescoreapiadmin.controller;

import com.gamescore.gamescoreapiadmin.dto.UpdateUserDTO;
import com.gamescore.gamescoreapiadmin.entity.User;
import com.gamescore.gamescoreapiadmin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List all users",
            tags = {"User"})
    public Flux<User> listAllUsers() {
        return userService.findAll();
    }

    @GetMapping(path = "{email}")
    @Operation(summary = "Find a user by email",
            tags = {"User"})
    public Mono<User> findUserById(@PathVariable final String email) {
        return userService.findByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user in database",
            tags = {"User"})
    public Mono<User> createUser(@Valid @RequestBody final User user){
        return  userService.createUser(user);
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update an user that already exist in database",
            tags = {"User"})
    public Mono<User> updateUser(@PathVariable final String email, @Valid @RequestBody final UpdateUserDTO updateUserDTO){
        return  userService.update(email, updateUserDTO);
    }

    @DeleteMapping(path = "{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an user in database",
            tags = {"User"})
    public Mono<Void> deleteUserByEmail(@PathVariable final String email){
        return  userService.delete(email);
    }
}
