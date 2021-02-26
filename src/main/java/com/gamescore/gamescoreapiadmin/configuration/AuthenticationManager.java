package com.gamescore.gamescoreapiadmin.configuration;

import com.gamescore.gamescoreapiadmin.util.JWTUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTUtils jwtUtils;

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        try {
            if (!jwtUtils.validateToken(authToken)) {
                return Mono.empty();
            }
            Claims claims = jwtUtils.getAllClaimsFromToken(authToken);
            List<String> rolesMap = Arrays.asList(claims.get("role").toString());

            List<GrantedAuthority> authorities = new ArrayList<>();
            for (String rolemap : rolesMap) {
                authorities.add(new SimpleGrantedAuthority(rolemap));
            }
            return Mono.just(new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities));
        } catch (Exception e) {
            log.error("Error on authenticate filer", e);
            return Mono.empty();
        }
    }
}
