package com.gamescore.gamescoreapiadmin.util;

import com.gamescore.gamescoreapiadmin.dto.UserDTO;
import com.gamescore.gamescoreapiadmin.entity.User;
import com.gamescore.gamescoreapiadmin.enumerator.UserRoles;

public class TestUtils {

    final public static String PASSWORD_ENCRYPTED = "OratEthmNxBftulcKMc65Q==";

    final public static String EMAIL_USER_ONE = "capitao@desumano.com";
    final public static String EMAIL_USER_TWO = "Joelho@titanio.com";

    final public static String PASSWORD = "Ney_lixo";

    public static User generateTestUserOne() {
        return generateUser(EMAIL_USER_ONE, "cansado", PASSWORD_ENCRYPTED, UserRoles.ADMIN.getRole());
    }

    public static User generateTestUserTwo() {
        return generateUser(EMAIL_USER_TWO, "Thacigod", PASSWORD_ENCRYPTED, UserRoles.USER.getRole());
    }

    public static UserDTO createNewUserDTO() {
        return UserDTO.builder()
                .email("jp@Caminha.campo")
                .name("lerdo")
                .password(PASSWORD_ENCRYPTED)
                .role(UserRoles.USER.getRole())
                .build();
    }

    private static User generateUser(final String email, final String name, final String encryptPassword, final String role) {
        return User.builder()
                .email(email)
                .name(name)
                .password(encryptPassword)
                .role(role)
                .build();
    }
}
