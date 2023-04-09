package va.vanthe.app_chat_2.ulitilies;

import java.lang.reflect.Field;
import java.util.HashMap;

public class EntityToHashMapConverter {
    public static HashMap<String, Object> toHashMap(Object obj) throws IllegalAccessException {
        HashMap<String, Object> entityMap = new HashMap<>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(obj);
            entityMap.put(field.getName(), value);
        }
        return entityMap;
    }
}
