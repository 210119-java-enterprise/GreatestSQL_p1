package com.revature.ObjectMapper;

import com.revature.GSQLogger.GSQLogger;
import com.revature.META.MetaConstructor;
import com.revature.META.MetaModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjectSaver extends ObjectMapper{
    public static final ObjectSaver objSaver = new ObjectSaver();

    private ObjectSaver() {
        super();
    }

    public static ObjectSaver getInstance() {
        return objSaver;
    }

    private String getColumns(final Collection<String> getters, final Optional<String> serial_name) {
        return String.join(",",getters.stream()
                    .filter(s -> (!serial_name.isPresent() || !s.equals(serial_name.get())))
                    .toArray(String[]::new));
    }

    private void setSerialID(final Object obj, final Optional<Map.Entry<Method,String[]>> setter,final PreparedStatement pstmt) {
        try {
            final ResultSet rs = pstmt.getGeneratedKeys();
            while (rs.next() && setter.isPresent()) {
                setter.get().getKey().invoke(obj,rs.getInt(setter.get().getValue()[0]));
            }
        } catch(SQLException | IllegalAccessException | InvocationTargetException sqle){
            GSQLogger.getInstance().writeError(sqle);
        }
    }

    public boolean saveObject(final Object obj,final Connection conn) {
        try {
            final MetaModel<?> model                           = MetaConstructor.getInstance().getModels().get(obj.getClass().getSimpleName());
            final HashMap<String,Method> getters               = model.getGetters();
            final Optional<String> serial_name                 = getSerialName(obj.getClass());
            final Optional<Map.Entry<Method, String[]>> setter = getSerialKeyEntry(serial_name, model.getSetters());
            final String args                                  = getArgs((serial_name.isPresent()) ? getters.keySet().size() - 2 : getters.keySet().size() - 1);
            final String columns                               = getColumns(getters.keySet(), serial_name);
            final String sql                                   = "INSERT INTO " + model.getTable_name() + " ( " + columns + " ) VALUES( " + args + " )";
            final PreparedStatement pstmt                      = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            final ParameterMetaData pd                         = pstmt.getParameterMetaData();
            int index = 1;
            for (Map.Entry<String,Method> getter : getters.entrySet()) {
                if (!serial_name.isPresent() || !getter.getKey().equals(setter.get().getValue()[0])) {
                    setStatement(pstmt, pd, getter.getValue(), obj, index++);
                }
            }
            if (pstmt.executeUpdate() != 0) {
                setSerialID(obj,setter,pstmt);
            }
            ObjectCache.getInstance().putObjInCache(obj);
            return true;
        } catch (SQLException sqle) {
            GSQLogger.getInstance().writeError(sqle);
        }
        return false;
    }
}
