package Meta;
import Annotatons.Column;
import Annotatons.Getter;
import Annotatons.Setter;
import Annotatons.Table;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public final class MetaConstructor {

    private static final MetaConstructor constructor = new MetaConstructor();
    private final HashMap<String,MetaModel<?>> models;


    private MetaConstructor() {
        super();
        models = new HashMap<>();
    }

    public static MetaConstructor getInstance() {
        return constructor;
    }
    public HashMap<String,MetaModel<?>> getModels() {
        return models;
    }

    private String getClassName(final Class<?> clazz) {
        return clazz.getSimpleName();
    }


    private Method[] getGetters(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.getDeclaredAnnotation(Getter.class) != null)
                .toArray(Method[]::new);
    }

    private Method[] getSetters(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.getDeclaredAnnotation(Setter.class) != null)
                .toArray(Method[]::new);
    }

    private Constructor<?> getConstructor(final Class<?> clazz) {
        return Arrays.stream(clazz.getConstructors())
                .filter(c -> c.getParameterTypes().length == 0)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private String getTableName(final Class<?> clazz) {
        return clazz.getDeclaredAnnotation(Table.class).name();
    }

    private String[] getColumnNames(final Field[] fields) {
        return Arrays.stream(fields)
                .filter(i -> i.getDeclaredAnnotation(Column.class) != null)
                .map(i -> i.getAnnotation(Column.class).name())
                .toArray(String[]::new);
    }

    private Field[] getFields(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .toArray(Field[]::new);
    }

    public void addModel(final Class<?> clazz) {
        final String class_name             = getClassName(clazz);
        final Method[] getters              = getGetters(clazz);
        final Method[] setters              = getSetters(clazz);
        final Constructor<?> constructor    = getConstructor(clazz);
        final String table_name             = getTableName(clazz);
        final String[] getter_column_names  = getColumnNames(getFields(clazz));
        models.put(class_name,new MetaModel<>(clazz,getters,setters,getter_column_names,constructor,table_name));
    }
}
