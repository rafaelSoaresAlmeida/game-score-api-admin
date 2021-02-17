package com.gamescore.gamescoreapiadmin.service;

import com.gamescore.gamescoreapiadmin.dto.UserDTO;
import com.gamescore.gamescoreapiadmin.entity.User;
import com.gamescore.gamescoreapiadmin.enumerator.UserMessages;
import com.gamescore.gamescoreapiadmin.enumerator.UserRoles;
import com.gamescore.gamescoreapiadmin.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("createUser creates a new user when successful")
    public void save_CreatesUser_whenSuccessful() {

        final User newUser = createUser();
        BDDMockito.when(userRepository.findByEmail(newUser.getEmail()))
                .thenReturn(Mono.empty());

        BDDMockito.when(userRepository.save(newUser))
                .thenReturn(Mono.just(newUser));

        StepVerifier.create(userService.create(newUser))
                .expectSubscription()
                .expectNext(newUser.withPassword(UserMessages.SENSITIVE_DATA.name()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Create user  return Mono error when an user with the same email already exist in database")
    public void save_ReturnsMonoError_whenUserAlreadyExistInDatabase() {

        final User newUser = createUser();
        BDDMockito.when(userRepository.findByEmail(newUser.getEmail()))
                .thenReturn(Mono.just(newUser));

        StepVerifier.create(userService.create(newUser))
                .expectSubscription()
                .expectErrorMessage("400 BAD_REQUEST \"".concat(UserMessages.USER_EMAIL_ALREADY_EXIST_DATABASE.name()).concat("\""))
                .verify();
    }

    @Test
    @DisplayName("Create user  return Mono error when an user role is invalid")
    public void save_ReturnsMonoError_whenUserRoleIsInvalid() {

        final User newUser = createUser();
        newUser.setRole("UltraAdmin");

        BDDMockito.when(userRepository.findByEmail(newUser.getEmail()))
                .thenReturn(Mono.empty());

        BDDMockito.when(userRepository.save(newUser))
                .thenReturn(Mono.just(newUser));

        StepVerifier.create(userService.create(newUser))
                .expectSubscription()
                .expectErrorMessage("400 BAD_REQUEST \"".concat(UserMessages.INVALID_USER_ROLE.name()).concat("\""))
                .verify();
    }

    @Test
    @DisplayName("Update save updated user and returns updated user mono when successful")
    public void update_SaveUpdateUser_whenSuccessful() {

        final User oldUser = createUser();
        final UserDTO userDTO = createUpdateUserDTO();
        final User userUpdated = User.builder()
                .name("Cavalo Cansado")
                .email("sobrancela@capitao.com")
                .password("desumano")
                .role(UserRoles.USER.name())
                .build();

        BDDMockito.when(userRepository.findByEmail(oldUser.getEmail()))
                .thenReturn(Mono.just(oldUser));

        BDDMockito.when(userRepository.save(userUpdated))
                .thenReturn(Mono.just(userUpdated));

        StepVerifier.create(userService.update(oldUser.getEmail(), userDTO))
                .expectSubscription()
                .expectNext(userUpdated).verifyComplete();
    }

    @Test
    @DisplayName("Update returns Mono Error when user does not exist")
    public void update_ReturnMonoError_whenEmptyMonoIsReturned() {

        final User oldUser = createUser();
        BDDMockito.when(userRepository.findByEmail(oldUser.getEmail()))
                .thenReturn(Mono.empty());

        final UserDTO userDTO = createUpdateUserDTO();

        StepVerifier.create(userService.update(oldUser.getEmail(), userDTO))
                .expectSubscription()
                .expectErrorMessage("404 NOT_FOUND \"".concat(UserMessages.USER_NOT_FOUND.name()).concat("\""))
                .verify();
    }

    @Test
    @DisplayName("Update returns Mono Error when user has an invalid role")
    public void update_ReturnMonoError_whenUserHasInvalidRole() {

        final User oldUser = createUser();
        final UserDTO userDTO = createUpdateUserDTO();
        userDTO.setRole("pipoca");
        BDDMockito.when(userRepository.findByEmail(oldUser.getEmail()))
                .thenReturn(Mono.just(oldUser));

        StepVerifier.create(userService.update(oldUser.getEmail(), userDTO))
                .expectSubscription()
                .expectErrorMessage("400 BAD_REQUEST \"".concat(UserMessages.INVALID_USER_ROLE.name()).concat("\""))
                .verify();
    }

    @Test
    @DisplayName("Delete removes the user when successful")
    public void delete_RemovesUser_whenSuccessful() {
        final User user = createUser();

        BDDMockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.just(user));

        BDDMockito.when(userRepository.delete(user))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.delete(user.getEmail()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("Delete returns Mono error when user does not exist")
    public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
        final User user = createUser();

        BDDMockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.delete(user.getEmail()))
                .expectErrorMessage("404 NOT_FOUND \"".concat(UserMessages.USER_NOT_FOUND.name()).concat("\""))
                .verify();
    }


    private User createUser() {
        return User.builder()
                .name("Tensei Shitara")
                .email("validUserTobeSaved")
                .role(UserRoles.ADMIN.name())
                .build();
    }

    private UserDTO createUpdateUserDTO() {
        return UserDTO.builder()
                .name("Cavalo Cansado")
                .email("sobrancela@capitao.com")
                .password("desumano")
                .role(UserRoles.USER.name())
                .build();
    }

}