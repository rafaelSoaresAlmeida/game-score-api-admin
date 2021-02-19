package com.gamescore.gamescoreapiadmin.util;

import com.gamescore.gamescoreapiadmin.enumerator.UserRoles;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@UtilityClass
public class ApplicationUtils {

    public static  <E extends Enum<E>> boolean isValidRole(final String roleName) {
        for (UserRoles e : UserRoles.class.getEnumConstants()) {
            if (e.getRole().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    public static String encodePassword(final String password){
        return StringUtils.isBlank(password) ? password : PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
    }
}
