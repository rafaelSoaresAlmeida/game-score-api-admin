package com.gamescore.gamescoreapiadmin.integration;

import com.gamescore.gamescoreapiadmin.dto.UserDTO;
import com.gamescore.gamescoreapiadmin.entity.User;
import com.gamescore.gamescoreapiadmin.enumerator.UserMessages;
import com.gamescore.gamescoreapiadmin.enumerator.UserRoles;
import com.gamescore.gamescoreapiadmin.repository.UserRepository;
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

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
public class GameScoreApiAdminIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    private User desumanoUser;
    private User thacigodUser;


    private void initializeData() {

        Flux<User> usersFlux = Flux.just(
                User.builder()
                        .email("capitao@desumano.com")
                        .name("Maicross")
                        .password("cavalo")
                        .role(UserRoles.ADMIN.name())
                        .build(),
                User.builder()
                        .email("Joelho@titanio.com")
                        .name("Thaci")
                        .password("thacigod")
                        .role(UserRoles.USER.name())
                        .build()
        );
        userRepository.deleteAll()
                .thenMany(usersFlux)
                .flatMap(userRepository::save)
                .blockLast();

        desumanoUser = User.builder()
                .email("capitao@desumano.com")
                .name("Maicross")
                .role(UserRoles.ADMIN.name())
                .build();

        thacigodUser = User.builder()
                .email("Joelho@titanio.com")
                .name("Thaci")
                .role(UserRoles.USER.name())
                .build();
    }


//    @BeforeAll
//    public void blockHoundSetup(){
//		BlockHound.install(builder -> builder.allowBlockingCallsInside("java.util.UUID", "randomUUID"));
//    }

    @BeforeEach
    public void setup() {
        initializeData();
    }

    @Test
    //  @WithUserDetails("cavalo")
    @DisplayName("listAll returns a flux of user")
    public void listAll_ReturnFluxOfUsers_WhenSuccessful() {
        webTestClient
                .get()
                .uri("/user")
                //     .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
                .exchange().expectStatus().is2xxSuccessful()
                .expectBodyList(User.class)
                .hasSize(2)
                .contains(desumanoUser, thacigodUser
                );
    }

    @Test
    @DisplayName("findByEmail returns a mono of user")
    public void findByEmail_ReturnMonoUser_WhenSuccessful() {
        webTestClient
                .get()
                .uri("/user/".concat(thacigodUser.getEmail()))
                //        .headers(headers -> headers.setBasicAuth("thacigod", "polivalente"))
                .exchange().expectStatus().is2xxSuccessful()
                .expectBody(User.class)
                .isEqualTo(thacigodUser);
    }

    @Test
    @DisplayName("findByEmail returns a Mono Error when user does not exist")
    public void findByEmail_ReturnMonoOfError_whenEmptyMonoIsReturned() {
        webTestClient
                .get()
                .uri("/user/jp@camera_lenta.com")
                //    .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
                .exchange().expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
        // .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("createUser creates an user when successful")
    public void createUser_CreatesUser_whenSuccessful() {
        final UserDTO userDTOToBeSaved = UserDTO.builder()
                .email("robinho@nao_.com")
                .name("generico")
                .password("fichaClean")
                .role(UserRoles.ADMIN.name())
                .build();

        webTestClient
                .post()
                .uri("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTOToBeSaved))
                //   .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .consumeWith(response -> response.getResponseBody().getName().equals("rob_generico"));
    }

    @Test
    @DisplayName("createUser return a mono error with bad request when some field is empty")
    public void createUser_ReturnsError_whenFieldIsEmpty() {
        final UserDTO userDTOToBeSaved = UserDTO.builder()
                .build();

        webTestClient
                .post()
                .uri("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTOToBeSaved))
                //     .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    @DisplayName("createUser return a mono error when user email already exist in database")
    public void createUser_ReturnsError_whenUserEmailAlreadyExistInDatabase() {
        final UserDTO userDTOToBeSaved = UserDTO.builder()
                .email("capitao@desumano.com")
                .name("generico")
                .password("fichaClean")
                .role(UserRoles.ADMIN.name()).build();

        webTestClient
                .post()
                .uri("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTOToBeSaved))
                //     .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("400 BAD_REQUEST \"".concat(UserMessages.USER_EMAIL_ALREADY_EXIST_DATABASE.name()).concat("\""));
    }

    @Test
    @DisplayName("createUser return a mono error when user role invalid")
    public void createUser_ReturnsError_whenUserRoleIsInvalid() {
        final UserDTO userDTOToBeSaved = UserDTO.builder()
                .email("jael@cruel.com")
                .name("generico")
                .password("basalto_chuteira")
                .role("Master_Blaster_role").build();

        webTestClient
                .post()
                .uri("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTOToBeSaved))
                //     .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("400 BAD_REQUEST \"".concat(UserMessages.INVALID_USER_ROLE.name()).concat("\""));
    }


    @Test
    @DisplayName("updateUser updated user and returns empty mono when successful")
    public void updateUser_SaveUpdateUser_whenSuccessful() {
        final UserDTO userDTO = UserDTO.builder()
                .email("jp@Caminha.campo")
                .name("lerdo")
                .password("nao_pifa")
                .role(UserRoles.USER.name())
                .build();

        webTestClient
                .put()
                .uri("/user/".concat(desumanoUser.getEmail()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTO))
                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();
    }

    @Test
    @DisplayName("updateUser returns Mono Error when user does not exist")
    public void updateUser_ReturnMonoError_whenEmptyMonoIsReturned() {
        final UserDTO userDTO = UserDTO.builder()
                .role(UserRoles.USER.name())
                .build();

        webTestClient
                .put()
                .uri("/user/mengao@agoraEuSouMengao")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDTO))
                //      .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
        //          .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }
//
//    @Test
//    @DisplayName("Update returns Mono Error with bad request when id is empty")
//    public void update_ReturnMonoError_whenIdIsEmpty() {
//        final Anime animeNewName = Anime.builder().name("Detonator Orgun Failed").build();
//        webTestClient
//                .put()
//                .uri("/animes/")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(animeNewName))
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange()
//                .expectStatus().is4xxClientError()
//                .expectBody()
//                .jsonPath("$.status").isEqualTo(405);
//    }
//
//    @Test
//    @DisplayName("Delete removes the anime when successful")
//    public void delete_RemovesAnime_whenSuccessful() {
//        webTestClient
//                .delete()
//                .uri("/animes/2")
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange()
//                .expectStatus().isNoContent()
//                .expectBody()
//                .isEmpty();
//    }
//
//    @Test
//    @DisplayName("Delete returns Mono error when anime does not exist")
//    public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
//        webTestClient
//                .delete()
//                .uri("/animes/99999")
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange()
//                .expectStatus().isNotFound()
//                .expectBody()
//                .jsonPath("$.status").isEqualTo(404)
//                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
//    }
//
//    @Test
//    @DisplayName("Delete returns Mono error with bad request when id is empty")
//    public void delete_ReturnMonoError_whenIdIsEmpty() {
//        webTestClient
//                .delete()
//                .uri("/animes")
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange()
//                .expectStatus().is4xxClientError()
//                .expectBody()
//                .jsonPath("$.status").isEqualTo(405);
//    }
}
