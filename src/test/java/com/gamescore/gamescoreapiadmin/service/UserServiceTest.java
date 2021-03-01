package com.gamescore.gamescoreapiadmin.service;

import com.gamescore.gamescoreapiadmin.configuration.PBKDF2Encoder;
import com.gamescore.gamescoreapiadmin.dto.UserDTO;
import com.gamescore.gamescoreapiadmin.entity.User;
import com.gamescore.gamescoreapiadmin.enumerator.UserMessages;
import com.gamescore.gamescoreapiadmin.repository.UserRepository;
import com.gamescore.gamescoreapiadmin.util.TestUtils;
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

    @Mock
    private PBKDF2Encoder pbkdf2Encoder;

    private User user;

    private UserDTO userDTO;

    @Test
    @DisplayName("createUser creates a new user when successful")
    public void save_CreatesUser_whenSuccessful() {

        user = TestUtils.generateTestUserOne();
        user.setPassword(TestUtils.PASSWORD);
        BDDMockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.empty());

        BDDMockito.when(userRepository.save(user))
                .thenReturn(Mono.just(user));

        BDDMockito.when(pbkdf2Encoder.encode(BDDMockito.any()))
                .thenReturn(TestUtils.PASSWORD);

        StepVerifier.create(userService.create(user))
                .expectSubscription()
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    @DisplayName("Create user  return Mono error when an user with the same email already exist in database")
    public void save_ReturnsMonoError_whenUserAlreadyExistInDatabase() {

        user = TestUtils.generateTestUserOne();
        BDDMockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.just(user));

        StepVerifier.create(userService.create(user))
                .expectSubscription()
                .expectErrorMessage("400 BAD_REQUEST \"".concat(UserMessages.USER_EMAIL_ALREADY_EXIST_DATABASE.name()).concat("\""))
                .verify();
    }

    @Test
    @DisplayName("Create user  return Mono error when an user role is invalid")
    public void save_ReturnsMonoError_whenUserRoleIsInvalid() {

        user = TestUtils.generateTestUserOne().withRole("UltraAdmin");

        BDDMockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.empty());

        BDDMockito.when(userRepository.save(user))
                .thenReturn(Mono.just(user));

        StepVerifier.create(userService.create(user))
                .expectSubscription()
                .expectErrorMessage("400 BAD_REQUEST \"".concat(UserMessages.INVALID_USER_ROLE.name()).concat("\""))
                .verify();
    }

    @Test
    @DisplayName("Update save updated user and returns updated user mono when successful")
    public void update_SaveUpdateUser_whenSuccessful() {

        user = TestUtils.generateTestUserOne();
        userDTO = TestUtils.createNewUserDTO();

        BDDMockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.just(user));

        BDDMockito.when(userRepository.save(BDDMockito.any()))
                .thenReturn(Mono.just(userDTO.toUser()));

        StepVerifier.create(userService.update(user.getEmail(), userDTO))
                .expectSubscription()
                .expectNext(userDTO.toUser()).verifyComplete();
    }

    @Test
    @DisplayName("Update returns Mono Error when user does not exist")
    public void update_ReturnMonoError_whenEmptyMonoIsReturned() {

        user = TestUtils.generateTestUserOne();
        BDDMockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.empty());

        userDTO = TestUtils.createNewUserDTO();

        StepVerifier.create(userService.update(user.getEmail(), userDTO))
                .expectSubscription()
                .expectErrorMessage("404 NOT_FOUND \"".concat(UserMessages.USER_NOT_FOUND.name()).concat("\""))
                .verify();
    }

    @Test
    @DisplayName("Update returns Mono Error when user has invalid role")
    public void update_ReturnMonoError_whenUserHasInvalidRole() {

        user = TestUtils.generateTestUserOne();
        userDTO = TestUtils.createNewUserDTO().withRole("pipoca");

        BDDMockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.just(user));

        StepVerifier.create(userService.update(user.getEmail(), userDTO))
                .expectSubscription()
                .expectErrorMessage("400 BAD_REQUEST \"".concat(UserMessages.INVALID_USER_ROLE.name()).concat("\""))
                .verify();
    }

    @Test
    @DisplayName("Delete removes the user with success")
    public void delete_RemovesUser_WithSuccess() {
        user = TestUtils.generateTestUserOne();

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

        BDDMockito.when(userRepository.findByEmail(TestUtils.EMAIL_USER_ONE))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.delete(TestUtils.EMAIL_USER_ONE))
                .expectErrorMessage("404 NOT_FOUND \"".concat(UserMessages.USER_NOT_FOUND.name()).concat("\""))
                .verify();
    }
}