package com.gamescore.gamescoreapiadmin.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoles {

    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private String role;

}
