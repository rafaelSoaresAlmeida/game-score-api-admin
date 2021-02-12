package com.gamescore.gamescoreapiadmin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Document(collection = "users")
@Data
@AllArgsConstructor
@Builder
@With
public class User {
    @Id
    @NotNull
    @NotEmpty(message = "The user email cannot be empty")
    private String email;

    @NotNull
    @NotEmpty(message = "The  user name cannot be empty")
    private String name;

    @NotNull
    @NotEmpty(message = "The user password cannot be empty")
    @JsonIgnore
    private String password;

    @NotNull
    @NotEmpty(message = "The user role cannot be empty")
    private String role;
}
