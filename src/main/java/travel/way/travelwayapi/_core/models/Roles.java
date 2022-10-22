package travel.way.travelwayapi._core.models;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Roles {
    public static final String ROLE_USER = "ROLE_USER";

    public static List<String> GetAllRoles() {
        List<String> result = new ArrayList<>();
        Field[] fields = Roles.class.getDeclaredFields();

        for (var field : fields) {
            try {
                if (field.getType().equals(String.class) && Modifier.isStatic(field.getModifiers())) {
                    result.add(field.getName().toString());
                }
            } catch (Exception ignored) {
            }
        }

        return result;
    }
}
