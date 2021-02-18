package com.revature.ObjectMapper;

import com.revature.GSQLogger.GSQLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ObjectCache {
    private final static ObjectCache obj_cache = new ObjectCache();
    private final HashMap<Class<?>, HashSet<Object>> cache;

    private ObjectCache() {
        super();
        cache = new HashMap<>();
    }

    public static ObjectCache getInstance() {
        return obj_cache;
    }

    public HashMap<Class<?>,HashSet<Object>> getCache() {
        return cache;
    }

    public void putObjInCache(final Object obj) {
        if(!cache.containsKey(obj.getClass())) {
            cache.put(obj.getClass(),new HashSet<>());
            System.out.println("new hashset");
        }
        System.out.println("adding to cache");
        cache.get(obj.getClass()).add(obj);
    }

    private boolean compareColumnToConditional(final Object obj,final HashMap<String,Method> getters,final String column,final String condition) {
        try {
            System.out.println("column is: " + column);
            System.out.println("condition is: " + condition);
            System.out.println("value is: " + getters.get(column).invoke(obj));
            return getters.get(column).invoke(obj).equals(condition);
        }catch(InvocationTargetException | IllegalAccessException e) {
            GSQLogger.getInstance().writeError(e);
        }
        return false;
    }
    private boolean compareValuesoFOperators(final Queue<Boolean> values,final String[] operators) {
        boolean value = true;
        for(String o: operators) {
            value = (o.equals("AND"))? values.remove() && values.remove(): values.remove() || values.remove();
        }
        return value;
    }

    private boolean compareObjects(final Object obj,final HashMap<String,Method> getters,final String[] columns,final String[] conditions,final String[] operators) {
        final Queue<Boolean> values = new LinkedList<>();
        for(int i = 0; i < columns.length; i++) {
            values.add(compareColumnToConditional(obj,getters,columns[i],conditions[i]));
        }
        return compareValuesoFOperators(values,operators);
    }

    public Optional<List<Object>> getObjFromCache(final Class<?> clazz,final HashMap<String,Method> getters,final String[] columns,final String[] conditions,final String[] operators) {
        if(!cache.containsKey(clazz)) {
            return Optional.empty();
        }
        try {
            final List<Object> list = new LinkedList<>();
            for(Object o: cache.get(clazz)){
                System.out.println("comparing");
                if(compareObjects(o,getters,columns,conditions,operators)) {
                    list.add(o);
                    System.out.println("true");
                }
            }
            return (list.size() > 0)? Optional.of(list) : Optional.empty();
        }catch(Exception e) {
            GSQLogger.getInstance().writeError(e);
        }
        return Optional.empty();
    }

    public void removeObjFromCache(final Object obj) {
        if(cache.containsKey(obj.getClass())) {
            cache.get(obj.getClass()).remove(obj);
        }
    }

}
