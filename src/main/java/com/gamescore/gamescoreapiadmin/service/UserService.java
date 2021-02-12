package com.gamescore.gamescoreapiadmin.service;

import com.gamescore.gamescoreapiadmin.dto.UpdateUserDTO;
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

    public Mono<User> createUser(User newUser) {
        log.info("Create a new user with email: {}", newUser.getEmail());
        return Mono.just(newUser.getEmail())
                .map(userRepository::findByEmail)
                .flatMap(userDb -> userDb.hasElement()
                        .flatMap(exist -> exist ? monoResponseStatusUserAlreadyExistInDatabaseException().log("User already exist")
                                : isValidEnumIgnoreCase(UserRoles.class, newUser.getRole()) ? userRepository.save(newUser).log("User created") : monoResponseStatusInvalidUserRoleException().log("Invalid user role"))
                        .then(Mono.just(newUser)));

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

    public Mono<User> update(final String email, final UpdateUserDTO updateUserDTO) {
        log.info("Update an user with email: {}", email);
        return Mono.just(email)
                .map(this::findByEmail)
                .flatMap(userFound -> {
                    if (!isValidEnumIgnoreCase(UserRoles.class, updateUserDTO.getRole())) {
                        return monoResponseStatusInvalidUserRoleException();
                    }
                    User userUpdated = updateUserFields(updateUserDTO, userFound.block());
                    userRepository.save(userUpdated).log("User updated");
                    return Mono.just(userUpdated);
                });
    }

    public Mono<Void> delete(final String email) {
        log.info("Delete an user with email: {}", email);
        return findByEmail(email)
                .flatMap(userRepository::delete).log("User deleted");
    }


    private User updateUserFields(final UpdateUserDTO updateUserDTO, final User userTobeSaved) {
        if (isNotBlank(updateUserDTO.getEmail())) {
            userTobeSaved.setEmail(updateUserDTO.getEmail());
        }

        if (isNotBlank(updateUserDTO.getName())) {
            userTobeSaved.setName(updateUserDTO.getName());
        }

        if (isNotBlank(updateUserDTO.getPassword())) {
            userTobeSaved.setPassword(updateUserDTO.getPassword());
        }

        if (isNotBlank(updateUserDTO.getRole())) {
            userTobeSaved.setRole(updateUserDTO.getRole());
        }

        return userTobeSaved;
    }


    public <T> Mono<T> monoResponseStatusNotFoundException() {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.name()));
    }

    public <T> Mono<T> monoResponseStatusUserAlreadyExistInDatabaseException() {
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, UserMessages.USER_ALREADY_EXIST_ON_DATABASE.name()));
    }

    public <T> Mono<T> monoResponseStatusInvalidUserRoleException() {
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, UserMessages.INVALID_USER_ROLE.name()));
    }
}
