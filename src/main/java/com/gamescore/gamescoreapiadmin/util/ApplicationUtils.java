package com.gamescore.gamescoreapiadmin.util;

import com.gamescore.gamescoreapiadmin.enumerator.UserRoles;
import lombok.experimental.UtilityClass;

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
}
