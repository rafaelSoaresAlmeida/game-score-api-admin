package com.gamescore.gamescoreapiadmin.repository;


import com.gamescore.gamescoreapiadmin.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByEmail(final String email);

    Mono<User> findByUsername(final String userName);

}
