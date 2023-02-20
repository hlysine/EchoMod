package echo.mechanics.duplicate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectFieldIterator {
    private static final Map<Class<?>, List<Field>> fieldCache = new HashMap<>();

    public static <T> void iterate(T object, FieldCallback callback) {
        iterate(object, callback, Object.class);
    }

    public static <T> void iterate(T object, FieldCallback callback, Class<?> stopClass) {
        if (object == null)
            return;
        Class<?> clazz = object.getClass();
        while (clazz != null && clazz != stopClass) {
            List<Field> fields = fieldCache.get(clazz);
            if (fields == null) {
                fields = new ArrayList<>();
                for (Field field : clazz.getDeclaredFields()) {
                    if (!field.isAccessible())
                        field.setAccessible(true);
                    int modifiers = field.getModifiers();
                    if (!Modifier.isStatic(modifiers)) {
                        fields.add(field);
                    }
                }
                fieldCache.put(clazz, fields);
            }
            for (Field field : fields) {
                try {
                    callback.accept(field);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public interface FieldCallback {
        void accept(Field field) throws Exception;
    }
}
