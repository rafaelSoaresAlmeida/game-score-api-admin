package com.gamescore.gamescoreapiadmin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserDTO {
    private String email;

    private String name;

    private String password;

    private String role;
}
