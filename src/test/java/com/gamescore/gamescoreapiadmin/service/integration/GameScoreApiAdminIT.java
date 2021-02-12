package com.gamescore.gamescoreapiadmin.service.integration;

import com.gamescore.gamescoreapiadmin.entity.User;
import com.gamescore.gamescoreapiadmin.enumerator.UserRoles;
import com.gamescore.gamescoreapiadmin.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
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

//    private void initializeSchema() {
//        this.client.sql(
//                "CREATE TABLE IF NOT EXISTS anime (id INT IDENTITY PRIMARY KEY, name VARCHAR(255));"
//                + "CREATE TABLE IF NOT EXISTS USER (ID INT IDENTITY PRIMARY KEY, NAME VARCHAR(255), USERNAME VARCHAR(255), PASSWORD VARCHAR(255), AUTHORITIES VARCHAR(255));")
//                .fetch()
//                .rowsUpdated()
//                .block();
//    }

    private void initializeData() {

//                userRepository.saveAll(Arrays.asList(
//                        User.builder().email("capitao@desumano.com").name("Maicross").password("cavalo").role(UserRoles.ADMIN.name()).build(),
//                        User.builder().email("Joelho@titanio.com").name("Thaci").password("thacigod").role(UserRoles.USER.name()).build()))
//                        .blockLast(Duration.ofSeconds(10));


        Flux<User> usersFlux = Flux.just(
                User.builder().email("capitao@desumano.com").name("Maicross").password("cavalo").role(UserRoles.ADMIN.name()).build(),
                User.builder().email("Joelho@titanio.com").name("Thaci").password("thacigod").role(UserRoles.USER.name()).build()
        );
        userRepository.deleteAll()
                .thenMany(usersFlux)
                .flatMap(userRepository::save)
                .blockLast();


    }

    ;
//        this.client.sql(
//                "insert into Anime (id, name) values (1, 'Full Metal');"
//                        + "insert into Anime (id, name) values (2, 'Hellsing');"
//                        + "insert into Anime (id, name) values (3, 'Attack Titan');"
//                        + "insert into Anime (id, name) values (4, 'Zeoraima');"
//                        // user data
//                        + "insert into user (id, name, username, password, authorities) values (101, 'Capitao Desumano', 'cavalo', '{bcrypt}$2a$10$B4JueaV/LHpVKSgw2skZteqT1m4OKIF8D6E/Vp1lqqJAl1xbZlyv.', 'ROLE_ADMIN,ROLE_USER');"
//                        + "insert into user (id, name, username, password, authorities) values (102, 'Thaci', 'thacigod', '{bcrypt}$2a$10$ScXipk72pD5kvpgcBBWqDuACWsX3VRchHyzPT05kOdarWLVXzIAm6', 'ROLE_USER');"
//
//
//        )
//                .fetch()
//                .rowsUpdated()
//                .block();
//    }

    private void cleanData() {
//        this.client.sql("DELETE FROM Anime;"
//                + "DELETE FROM User;")
//                .fetch()
//                .rowsUpdated()
//                .block();
    }

//    @BeforeAll
//    public void blockHoundSetup(){
//		BlockHound.install(builder -> builder.allowBlockingCallsInside("java.util.UUID", "randomUUID"));
//    }

    @BeforeEach
    public void setup() {
        //    initializeSchema();
        cleanData();
        initializeData();
    }

    private HttpHeaders buildHttpHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    //  @WithUserDetails("cavalo")
    @DisplayName("listAll returns a flux of animes")
    public void listAll_ReturnFluxOfUsers_WhenSuccessful() {
        webTestClient
                .get()
                .uri("/user")
                //     .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
                .exchange().expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.[0].email").isEqualTo("capitao@desumano.com")
                .jsonPath("$.[0].name").isEqualTo("Maicross");
    }

//    @Test
//    @DisplayName("listAll Flavor 2 returns a flux of animes")
//    public void listAll_Flavor2_ReturnFluxOfAnime_WhenSuccessful() {
//        webTestClient
//                .get()
//                .uri("/animes")
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange().expectStatus().is2xxSuccessful()
//                .expectBodyList(Anime.class)
//                .hasSize(4);
//    }
//
//    @Test
//    @DisplayName("findById returns a mono of animes")
//    public void findById_ReturnMonoAnime_WhenSuccessful() {
//        webTestClient
//                .get()
//                .uri("/animes/2")
//                .headers(headers -> headers.setBasicAuth("thacigod", "polivalente"))
//                .exchange().expectStatus().is2xxSuccessful()
//                .expectBody(Anime.class)
//                .isEqualTo(Anime.builder().id(2).name("Hellsing").build());
//    }
//
//    @Test
//    @DisplayName("FindById returns a Mono Error when anime does not exist")
//    public void findById_ReturnMonoOfError_whenEmptyMonoIsReturned() {
//        webTestClient
//                .get()
//                .uri("/animes/69")
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange().expectStatus().isNotFound()
//                .expectBody()
//                .jsonPath("$.status").isEqualTo(404)
//                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
//
//    }
//
//    @Test
//    @DisplayName("Save creates an anime when successful")
//    public void save_CreatesAnime_whenSuccessful() {
//        final Anime animeToBeSaved = Anime.builder().name("Afro Samurai").build();
//
//        webTestClient
//                .post()
//                .uri("/animes/")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(animeToBeSaved))
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(Anime.class)
//                .consumeWith(response -> response.getResponseBody().getName().equals("Afro Samurai"));
//    }
//
//    @Test
//    @DisplayName("Save return a mono error with bad request when name is empty")
//    public void save_ReturnsError_whenNameIsEmpty() {
//        final Anime animeToBeSaved = Anime.builder().build();
//
//        webTestClient
//                .post()
//                .uri("/animes/")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(animeToBeSaved))
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody()
//                .jsonPath("$.status").isEqualTo(400);
//    }
//
//    @Test
//    @DisplayName("SaveBatch creates a list of anime when successful")
//    public void saveBatch_CreatesListAnime_whenSuccessful() {
//        final Anime animeToBeSaved = Anime.builder().name("Afro Samurai").build();
//        webTestClient
//                .post()
//                .uri("/animes/batch")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved)))
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBodyList(Anime.class)
//                .hasSize(2);
//    }
//
//    @Test
//    @DisplayName("SaveBatch return Mono error when one of the objects in the list contains null or empty name")
//    public void saveBatch_ReturnsMonoError_whenContainsInvalidName() {
//        final Anime animeToBeSaved = Anime.builder().name("Afro Samurai").build();
//        webTestClient
//                .post()
//                .uri("/animes/batch")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved.withName(""))))
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody()
//                .jsonPath("$.status").isEqualTo(400)
//                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
//    }
//
//    @Test
//    @DisplayName("UpdateSave updated anime and returns empty mono when successful")
//    public void update_SaveUpdateAnime_whenSuccessful() {
//        final Anime animeNewName = Anime.builder().name("Detonator Orgun").build();
//        webTestClient
//                .put()
//                .uri("/animes/2")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(animeNewName))
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange()
//                .expectStatus().isNoContent()
//                .expectBody()
//                .isEmpty();
//    }
//
//    @Test
//    @DisplayName("Update returns Mono Error when anime does not exist")
//    public void update_ReturnMonoError_whenEmptyMonoIsReturned() {
//        final Anime animeNewName = Anime.builder().name("Detonator Orgun Failed").build();
//        webTestClient
//                .put()
//                .uri("/animes/666")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(animeNewName))
//                .headers(headers -> headers.setBasicAuth("cavalo", "cansado"))
//                .exchange()
//                .expectStatus().isNotFound()
//                .expectBody()
//                .jsonPath("$.status").isEqualTo(404)
//                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
//    }
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
