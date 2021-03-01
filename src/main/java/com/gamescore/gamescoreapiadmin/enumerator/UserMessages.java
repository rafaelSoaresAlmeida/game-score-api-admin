package com.gamescore.gamescoreapiadmin.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserMessages {

    USER_EMAIL_ALREADY_EXIST_DATABASE("001-User email already exist on database"),
    USER_NOT_FOUND("002-User not found"),
    INVALID_USER_ROLE("003-Invalid user role"),
    USER_NOT_AUTHENTICATED("004-User not authenticated"),
    USER_NOT_AUTHORIZED_ACCESS_RESOURCE("005-User not authorized to access this resource");

    private String message;
}
