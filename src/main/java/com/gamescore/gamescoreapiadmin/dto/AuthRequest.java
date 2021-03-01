package com.gamescore.gamescoreapiadmin.dto;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.io.Serializable;

@Data
@Builder
@With
public class AuthRequest implements Serializable {

    private String username;
    private String password;
}
