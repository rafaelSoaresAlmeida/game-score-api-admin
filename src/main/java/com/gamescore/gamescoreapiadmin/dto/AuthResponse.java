package com.gamescore.gamescoreapiadmin.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AuthResponse implements Serializable {

    private String token;
}
