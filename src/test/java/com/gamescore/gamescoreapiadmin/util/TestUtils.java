package com.gamescore.gamescoreapiadmin.util;

import com.gamescore.gamescoreapiadmin.dto.UserDTO;
import com.gamescore.gamescoreapiadmin.entity.User;
import com.gamescore.gamescoreapiadmin.enumerator.UserRoles;

public class TestUtils {

    final private static String passwordEncryptedUserOne = "{bcrypt}$2a$10$dC5p8icscnfUlb6MZAvGK.GTSeg7U9Lmouz.u8LweRdKtxUlgwrrq";
    final private static String passwordEncryptedUserTwo = "{bcrypt}$2a$10$btM5.FA1BlbazQjo79E6MedKLlDE5PsSab5tCOkdkuHLb6xztWZSC";

    final public static String emailUserOne = "capitao@desumano.com";
    final public static String emailUserTwo = "Joelho@titanio.com";

    final public static String passwordUserOne = "cavalo";
    final public static String passwordUserTwo = "polivalente";

    public static User generateTestUserOne() {
        return generateUser(emailUserOne, "cansado", passwordEncryptedUserOne, UserRoles.ADMIN.getRole());
    }

    public static User generateTestUserTwo() {
        return generateUser(emailUserTwo, "Thacigod", passwordEncryptedUserTwo, UserRoles.USER.getRole());
    }

    public static UserDTO createNewUserDTO() {
        return UserDTO.builder()
                .email("jp@Caminha.campo")
                .name("lerdo")
                .password("nao_pifa")
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
