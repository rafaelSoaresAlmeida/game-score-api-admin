package com.gamescore.gamescoreapiadmin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@AllArgsConstructor
@Builder
@With
public class User {
    @Id
    private String email;

    private String name;

    @JsonIgnore
    private String password;

    private String role;
}
