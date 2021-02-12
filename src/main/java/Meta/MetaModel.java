package Meta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class MetaModel<T> {
    private final Class<T> clazz;
    private final Method[] getters;
    private final Method[] setters;
    private final String[] getter_columns;
    private final Constructor<?> constructor;
    private final String table_name;

    public Class<T> getClazz() {
        return clazz;
    }

    public Method[] getGetters() {
        return getters;
    }

    public Method[] getSetters() {
        return setters;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public String getTable_name() {
        return table_name;
    }

    public String[] getGetter_columns() {
        return getter_columns;
    }

    public MetaModel(Class<T> clazz, Method[] getters, Method[] setters, String[] getter_columns, Constructor<?> constructor, String table_name) {
        this.clazz = clazz;
        this.getters = getters;
        this.setters = setters;
        this.constructor = constructor;
        this.table_name = table_name;
        this.getter_columns = getter_columns;
    }

}
