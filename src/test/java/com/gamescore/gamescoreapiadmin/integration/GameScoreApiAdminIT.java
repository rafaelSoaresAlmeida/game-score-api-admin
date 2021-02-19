package com.gamescore.gamescoreapiadmin.integration;

import com.gamescore.gamescoreapiadmin.dto.UserDTO;
import com.gamescore.gamescoreapiadmin.entity.User;
import com.gamescore.gamescoreapiadmin.enumerator.UserMessages;
import com.gamescore.gamescoreapiadmin.enumerator.UserRoles;
import com.gamescore.gamescoreapiadmin.repository.UserRepository;
import com.gamescore.gamescoreapiadmin.util.TestUtils;
import de.bwaldvogel.mongo.backend.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import static com.gamescore.gamescoreapiadmin.util.TestUtils.EMAIL_USER_ONE;
import static com.gamescore.gamescoreapiadmin.util.TestUtils.PASSWORD_USER_ONE;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
public class GameScoreApiAdminIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    private User testUserOneResponse;
    private User testUserTwoResponse;
    private UserDTO userDTO;


    private void initializeData() {

        Flux<User> usersFlux = Flux.just(
                TestUtils.generateTestUserOne(),
                TestUtils.generateTestUserTwo()
        );
        userRepository.deleteAll()
                .thenMany(usersFlux)
                .flatMap(userRepository::save)
                .blockLast();

        testUserOneResponse = TestUtils.generateTestUserOne().withPassword(null);

        testUserTwoResponse = TestUtils.generateTestUserTwo().withPassword(null);
    }

    @BeforeEach
    public void setup() {
        initializeData();
    }

    @Test
    @DisplayName("listAll returns two users with success")
    public void listAll_ReturnUsers_WithSuccess() {
        webTestClient
                .get()
                .uri("/user")
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange().expectStatus().is2xxSuccessful()
                .expectBodyList(User.class)
                .hasSize(2)
                .contains(testUserOneResponse, testUserTwoResponse);
    }

    @Test
    @DisplayName("listAll returns 403 FORBIDDEN when user not have admin acess")
    public void listAll_Return403_WhenUserNotHaveAdminAccess() {
        webTestClient
                .get()
                .uri("/user")
                .headers(headers -> headers.setBasicAuth(TestUtils.EMAIL_USER_TWO, TestUtils.PASSWORD_USER_TWO))
                .exchange().expectStatus().isForbidden();
    }

    @Test
    @DisplayName("findByEmail returns a mono of user")
    public void findByEmail_ReturnUser_WithSuccess() {
        webTestClient
                .get()
                .uri("/user/".concat(testUserTwoResponse.getEmail()))
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange().expectStatus().is2xxSuccessful()
                .expectBody(User.class)
                .isEqualTo(testUserTwoResponse);
    }

    @Test
    @DisplayName("findByEmail returns 404 NOT FOUND when user does not exist")
    public void findByEmail_Return404_whenUserNotFound() {
        webTestClient
                .get()
                .uri("/user/jp@camera_lenta.com")
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange().expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    @DisplayName("createUser creates an user with success")
    public void createUser_CreatesUser_WithSuccess() {
        userDTO = TestUtils.createNewUserDTO();

        webTestClient
                .post()
                .uri("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTO))
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .consumeWith(response -> {
                    Assert.equals(response.getResponseBody().getEmail(), userDTO.getEmail());
                    Assert.equals(response.getResponseBody().getName(), userDTO.getName());
                    Assert.isNull(response.getResponseBody().getPassword());
                    Assert.equals(response.getResponseBody().getRole(), UserRoles.USER.getRole());
                });
    }

    @Test
    @DisplayName("createUser return 400 BAD REQUEST when some field is empty")
    public void createUser_Returns400_WhenFieldIsEmpty() {
        userDTO = UserDTO.builder()
                .build();

        webTestClient
                .post()
                .uri("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTO))
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    @DisplayName("createUser return a 400 BAD REQUEST when user email already exist in database")
    public void createUser_Returns400_WhenUserEmailAlreadyExistInDatabase() {
        userDTO = TestUtils.createNewUserDTO()
                .withEmail(testUserOneResponse.getEmail());

        webTestClient
                .post()
                .uri("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTO))
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("400 BAD_REQUEST \"".concat(UserMessages.USER_EMAIL_ALREADY_EXIST_DATABASE.name()).concat("\""));
    }

    @Test
    @DisplayName("createUser return a BAD REQUEST when user role invalid")
    public void createUser_Returns400_WhenUserRoleIsInvalid() {
        userDTO = TestUtils.createNewUserDTO()
                .withRole("Master_Blaster_role");

        webTestClient
                .post()
                .uri("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTO))
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("400 BAD_REQUEST \"".concat(UserMessages.INVALID_USER_ROLE.name()).concat("\""));
    }

    @Test
    @DisplayName("updateUser updated an user with success")
    public void updateUser_SaveUpdateUser_WithSuccess() {
        userDTO = TestUtils.createNewUserDTO();

        webTestClient
                .put()
                .uri("/user/".concat(testUserTwoResponse.getEmail()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTO))
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(User.class)
                .consumeWith(response -> {
                    Assert.equals(response.getResponseBody().getEmail(), userDTO.getEmail());
                    Assert.equals(response.getResponseBody().getName(), userDTO.getName());
                    Assert.isNull(response.getResponseBody().getPassword());
                    Assert.equals(response.getResponseBody().getRole(), UserRoles.USER.getRole());
                });
    }

    @Test
    @DisplayName("updateUser just update user role with success")
    public void updateUser_SaveUpdateUserRole_WithSuccess() {
        userDTO = UserDTO.builder()
                .role(UserRoles.ADMIN.getRole())
                .build();

        webTestClient
                .put()
                .uri("/user/".concat(testUserTwoResponse.getEmail()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTO))
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(User.class)
                .consumeWith(response -> {
                    Assert.equals(response.getResponseBody().getEmail(), testUserTwoResponse.getEmail());
                    Assert.equals(response.getResponseBody().getName(), testUserTwoResponse.getName());
                    Assert.isNull(response.getResponseBody().getPassword());
                    Assert.equals(response.getResponseBody().getRole(), UserRoles.ADMIN.getRole());
                });
    }

    @Test
    @DisplayName("updateUser returns 404 NOT FOUND Error when user does not exist")
    public void updateUser_Return404_WhenUserNotExist() {
        userDTO = TestUtils.createNewUserDTO();

        webTestClient
                .put()
                .uri("/user/mengao@agoraEuSouMengao")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTO))
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    @DisplayName("updateUser returns 400 BAD REQUEST when user role is invalid")
    public void updateUser_Return400_WhenUserRoleIsInvalid() {
        userDTO = TestUtils.createNewUserDTO().withRole("MengaoRole");

        webTestClient
                .put()
                .uri("/user/".concat(testUserTwoResponse.getEmail()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTO))
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("400 BAD_REQUEST \"".concat(UserMessages.INVALID_USER_ROLE.name()).concat("\""));
    }

    @Test
    @DisplayName("Update returns 405 METHOD NOT ALLOWED  when user email(query param) is empty")
    public void update_Return405_WhenEmailIsEmpty() {
        userDTO = TestUtils.createNewUserDTO();

        webTestClient
                .put()
                .uri("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTO))
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(405);
    }

    @Test
    @DisplayName("Delete removes the user with success")
    public void delete_RemovesUser_WithSuccess() {
        webTestClient
                .delete()
                .uri("/user/".concat(testUserTwoResponse.getEmail()))
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();
    }

    @Test
    @DisplayName("Delete 404 NOT FOUND when user does not exist")
    public void delete_Return404_WhenUserNotExist() {
        webTestClient
                .delete()
                .uri("/user/vander@Delay.naoPegaPenalty")
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    @DisplayName("Delete returns 405 METHOD NOT ALLOWED when email (query param) is empty")
    public void delete_Return405_WhenEmailIsEmpty() {
        webTestClient
                .delete()
                .uri("/user")
                .headers(headers -> headers.setBasicAuth(EMAIL_USER_ONE, PASSWORD_USER_ONE))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(405);
    }
}
