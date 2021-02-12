package com.gamescore.gamescoreapiadmin.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserMessages {

    USER_ALREADY_EXIST_ON_DATABASE("001-User already exist on database"),
    USER_NOT_FOUND("002-User not found"),
    INVALID_USER_ROLE("003-Invalid user role");

    private String message;
}
