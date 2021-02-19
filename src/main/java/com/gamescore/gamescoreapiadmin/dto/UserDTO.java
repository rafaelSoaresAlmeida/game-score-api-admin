package com.gamescore.gamescoreapiadmin.dto;

import com.gamescore.gamescoreapiadmin.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@With
public class UserDTO {

    @NotNull
    @NotEmpty(message = "The user email cannot be empty")
    private String email;

    @NotNull
    @NotEmpty(message = "The  user name cannot be empty")
    private String name;

    @NotNull
    @NotEmpty(message = "The user password cannot be empty")
    private String password;

    @NotNull
    @NotEmpty(message = "The user role cannot be empty")
    private String role;

    public User toUser() {
        return User.builder()
                .email(this.email)
                .name(this.name)
                .password(this.password)
                .role(this.role)
                .build();
    }
}
