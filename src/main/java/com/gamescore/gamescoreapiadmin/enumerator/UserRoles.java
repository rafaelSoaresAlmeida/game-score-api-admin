package com.gamescore.gamescoreapiadmin.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoles {

    ADMIN("ADMIN"),
    USER("USER");

    private String message;
}
