package com.gamescore.gamescoreapiadmin.service;

import com.gamescore.gamescoreapiadmin.configuration.PBKDF2Encoder;
import com.gamescore.gamescoreapiadmin.dto.AuthRequest;
import com.gamescore.gamescoreapiadmin.dto.AuthResponse;
import com.gamescore.gamescoreapiadmin.entity.User;
import com.gamescore.gamescoreapiadmin.enumerator.UserMessages;
import com.gamescore.gamescoreapiadmin.enumerator.UserRoles;
import com.gamescore.gamescoreapiadmin.repository.UserRepository;
import com.gamescore.gamescoreapiadmin.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JWTUtils jwtUtils;

    private final PBKDF2Encoder passwordEncoder;

    private final UserRepository userRepository;

    public Mono<AuthResponse> login(final AuthRequest authRequest) {
        return Mono.just(authRequest.getUsername())
                .map(this::findByEmail)
                .flatMap(userDb -> {
                            return userDb.flatMap(userDetails -> {

                                if (isNull(userDetails)) {
                                    return monoResponseUnauthorizedException();
                                }

                                if (!userDetails.getRole().equals(UserRoles.ADMIN.getRole())) {
                                    return monoResponseForbiddenException();
                                }

                                return passwordEncoder.encode(authRequest.getPassword()).equals(userDetails.getPassword())
                                        ? Mono.just(AuthResponse.builder().token(jwtUtils.generateToken(userDetails)).build())
                                        : monoResponseUnauthorizedException();
                            });
                        }
                );
    }

    private Mono<User> findByEmail(final String email) {
        return userRepository.findByEmail(email).switchIfEmpty(
                monoResponseUnauthorizedException());
    }

    public <T> Mono<T> monoResponseForbiddenException() {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, UserMessages.USER_NOT_AUTHORIZED_ACCESS_RESOURCE.name()));
    }

    public <T> Mono<T> monoResponseUnauthorizedException() {
        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, UserMessages.USER_NOT_AUTHENTICATED.name()));
    }
}
