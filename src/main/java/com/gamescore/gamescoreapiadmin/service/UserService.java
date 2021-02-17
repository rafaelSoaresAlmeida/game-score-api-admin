package com.gamescore.gamescoreapiadmin.service;

import com.gamescore.gamescoreapiadmin.dto.UserDTO;
import com.gamescore.gamescoreapiadmin.entity.User;
import com.gamescore.gamescoreapiadmin.enumerator.UserMessages;
import com.gamescore.gamescoreapiadmin.enumerator.UserRoles;
import com.gamescore.gamescoreapiadmin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.EnumUtils.isValidEnumIgnoreCase;
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
                                : isValidEnumIgnoreCase(UserRoles.class, newUser.getRole()) ? userRepository.save(newUser).log("User created") : monoResponseStatusInvalidUserRoleException().log("Invalid user role"))
                        .then(Mono.just(newUser.withPassword(UserMessages.SENSITIVE_DATA.name()))));

// not working .. I don't know why
//        return userRepository.findByEmail(newUser.getEmail())
//                .flatMap(userFound -> {
//                    if (Objects.nonNull(userFound)) {
//                       return monoResponseStatusUserAlreadyExistInDatabaseException().log("User already exist");
//                    }
//                    userRepository.save(newUser);
//                    return newUser;
//                });

    }

    public Mono<User> update(final String email, final UserDTO userDTO) {
        log.info("Update an user with email: {}", email);
        return Mono.just(email)
                .map(this::findByEmail)
                .flatMap(userFound -> {
                    if (!isValidEnumIgnoreCase(UserRoles.class, userDTO.getRole())) {
                        return monoResponseStatusInvalidUserRoleException();
                    }
                    User userUpdated = updateUserFields(userDTO, userFound.block());
                    userRepository.save(userUpdated).log("User updated");
                    return Mono.just(userUpdated);
                });
    }

    public Mono<Void> delete(final String email) {
        log.info("Delete an user with email: {}", email);
        return findByEmail(email)
                .flatMap(userRepository::delete).log("User deleted");
    }

    private User updateUserFields(final UserDTO userDTO, final User userTobeSaved) {
        if (isNotBlank(userDTO.getEmail())) {
            userTobeSaved.setEmail(userDTO.getEmail());
        }

        if (isNotBlank(userDTO.getName())) {
            userTobeSaved.setName(userDTO.getName());
        }

        if (isNotBlank(userDTO.getPassword())) {
            userTobeSaved.setPassword(userDTO.getPassword());
        }

        if (isNotBlank(userDTO.getRole())) {
            userTobeSaved.setRole(userDTO.getRole());
        }

        return userTobeSaved;
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
