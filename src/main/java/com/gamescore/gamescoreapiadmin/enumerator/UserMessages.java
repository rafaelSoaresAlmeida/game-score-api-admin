package com.gamescore.gamescoreapiadmin.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserMessages {

    USER_EMAIL_ALREADY_EXIST_DATABASE("001-User email already exist on database"),
    USER_NOT_FOUND("002-User not found"),
    INVALID_USER_ROLE("003-Invalid user role"),
    SENSITIVE_DATA("<sensitive data>");

    private String message;
}
