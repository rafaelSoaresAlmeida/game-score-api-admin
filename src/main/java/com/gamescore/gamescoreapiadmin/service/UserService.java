package com.gamescore.gamescoreapiadmin.service;

import com.gamescore.gamescoreapiadmin.dto.UserDTO;
import com.gamescore.gamescoreapiadmin.entity.User;
import com.gamescore.gamescoreapiadmin.enumerator.UserMessages;
import com.gamescore.gamescoreapiadmin.repository.UserRepository;
import com.gamescore.gamescoreapiadmin.util.ApplicationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> findByEmail(final String email) {
        return userRepository.findByEmail(email).switchIfEmpty(
                monoResponseStatusNotFoundException());
    }

    public Mono<User> create(final User newUser) {
        log.info("Create a new user with email: {}", newUser.getEmail());
        return Mono.just(newUser.getEmail())
                .map(userRepository::findByEmail)
                .flatMap(userDb -> userDb.hasElement()
                        .flatMap(exist -> exist ? monoResponseStatusUserEmailAlreadyExistInDatabaseException().log("User already exist")
                                : ApplicationUtils.isValidRole(newUser.getRole()) ? userRepository
                                .save(newUser.withPassword(ApplicationUtils.encodePassword(newUser.getPassword()))).log("User created")
                                : monoResponseStatusInvalidUserRoleException().log("Invalid user role ->" + newUser.getRole()))
                        .then(Mono.just(newUser)));
    }

    public Mono<User> update(final String email, final UserDTO userDTO) {
        log.info("Update an user with email: {}", email);
        return Mono.just(email)
                .map(this::findByEmail)
                .flatMap(userFound -> {
                    final Mono<User> userUpdated = updateUserFields(userDTO, userFound);
                    return userUpdated.flatMap(user -> userRepository.save(user).log("User updated"));
                });
    }

    public Mono<Void> delete(final String email) {
        log.info("Delete an user with email: {}", email);
        return findByEmail(email)
                .flatMap(userRepository::delete).log("User deleted");
    }

    private Mono<User> updateUserFields(final UserDTO userDTO, final Mono<User> userTobeUpdate) {
        return userTobeUpdate.flatMap(userTobeSaved -> {
            if (isNotBlank(userDTO.getEmail())) {
                userTobeSaved.setEmail(userDTO.getEmail());
            }

            if (isNotBlank(userDTO.getName())) {
                userTobeSaved.setName(userDTO.getName());
            }

            if (isNotBlank(userDTO.getPassword())) {
                userTobeSaved.setPassword(ApplicationUtils.encodePassword(userDTO.getPassword()));
            }

            if (isNotBlank(userDTO.getRole())) {
                if (!ApplicationUtils.isValidRole(userDTO.getRole())) {
                    return monoResponseStatusInvalidUserRoleException();
                }

                userTobeSaved.setRole(userDTO.getRole());
            }
            return Mono.just(userTobeSaved);
        });
    }

    public <T> Mono<T> monoResponseStatusNotFoundException() {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.name()));
    }

    public <T> Mono<T> monoResponseStatusUserEmailAlreadyExistInDatabaseException() {
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, UserMessages.USER_EMAIL_ALREADY_EXIST_DATABASE.name()));
    }

    public <T> Mono<T> monoResponseStatusInvalidUserRoleException() {
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, UserMessages.INVALID_USER_ROLE.name()));
    }
}
