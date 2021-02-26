package com.gamescore.gamescoreapiadmin.controller;

import com.gamescore.gamescoreapiadmin.configuration.PBKDF2Encoder;
import com.gamescore.gamescoreapiadmin.dto.AuthRequest;
import com.gamescore.gamescoreapiadmin.dto.AuthResponse;
import com.gamescore.gamescoreapiadmin.service.UserService;
import com.gamescore.gamescoreapiadmin.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JWTUtils jwtUtils;

    private final PBKDF2Encoder passwordEncoder;

    private final UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest ar) {
        return userService.findByEmail(ar.getUsername()).map((userDetails) -> {
            if (passwordEncoder.encode(ar.getPassword()).equals(userDetails.getPassword())) {
                return ResponseEntity.ok(AuthResponse.builder().token(jwtUtils.generateToken(userDetails)).build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
