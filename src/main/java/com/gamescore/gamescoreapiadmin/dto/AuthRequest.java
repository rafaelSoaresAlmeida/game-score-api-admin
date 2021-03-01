package com.gamescore.gamescoreapiadmin.dto;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@With
public class AuthRequest implements Serializable {

    @NotNull
    @NotEmpty(message = "The user name cannot be empty")
    private String username;

    @NotNull
    @NotEmpty(message = "The password cannot be empty")
    private String password;
}
