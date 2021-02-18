package com.revature.META;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;

public class MetaModel<T> {
    private final Class<T> clazz;
    private final HashMap<String,Method> getters;
    private final HashMap<Method,String[]> setters;
    private final Constructor<?> constructor;
    private final String table_name;

    public Class<T> getClazz() {
        return clazz;
    }

    public HashMap<String,Method> getGetters() {
        return getters;
    }

    public HashMap<Method, String[]> getSetters() {
        return setters;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public String getTable_name() {
        return table_name;
    }

    public MetaModel(Class<T> clazz,HashMap<String,Method> getters,HashMap<Method,String[]> setters, Constructor<?> constructor, String table_name) {
        this.clazz = clazz;
        this.getters = getters;
        this.setters = setters;
        this.constructor = constructor;
        this.table_name = table_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaModel<?> metaModel = (MetaModel<?>) o;
        return clazz.equals(metaModel.clazz)
                && constructor.equals(metaModel.constructor)
                && table_name.equals(metaModel.table_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, constructor, table_name);
    }

    @Override
    public String toString() {
        return "MetaModel{" +
                "clazz=" + clazz +
                ", getters=" + getters +
                ", setters=" + setters +
                ", constructor=" + constructor +
                ", table_name='" + table_name + '\'' +
                '}';
    }
}
