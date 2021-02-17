package ObjectMapper;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

import Logger.GSQLogger;
import Meta.MetaConstructor;
import Meta.MetaModel;
import Connection.ConnectionFactory;

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
                System.out.println("setting serial id");
                setter.get().getKey().invoke(obj,rs.getInt(setter.get().getValue()[0]));
            }
        } catch(SQLException | IllegalAccessException | InvocationTargetException sqle){
            GSQLogger.getInstance().writeError(sqle);
        }
    }

    public boolean saveObject(final Object obj,final Connection conn) {
        try {
            final MetaModel<?> model                           = MetaConstructor.getInstance().getModels().get(obj.getClass().getSimpleName());
            final HashMap<Method, String> getters              = model.getGetters();
            final Optional<String> serial_name                 = getSerialName(obj);
            final Optional<Map.Entry<Method, String[]>> setter = getSerialKeyEntry(serial_name, model.getSetters());
            final String args                                  = getArgs((serial_name.isPresent()) ? getters.keySet().size() - 2 : getters.keySet().size() - 1);
            final String columns                               = getColumns(getters.values(), serial_name);
            final String sql                                   = "INSERT INTO " + model.getTable_name() + " ( " + columns + " ) VALUES( " + args + " )";
            final PreparedStatement pstmt                      = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            final ParameterMetaData pd                         = pstmt.getParameterMetaData();
            int index = 1;
            for (Map.Entry<Method, String> getter : getters.entrySet()) {
                if (!serial_name.isPresent() || !getter.getValue().equals(setter.get().getValue()[0])) {
                    System.out.println("name is: " + getter.getValue());
                    setStatement(pstmt, pd, getter.getKey(), obj, index++);
                }
            }
            if (pstmt.executeUpdate() != 0) {
                setSerialID(obj,setter,pstmt);
            }
            return true;
        } catch (SQLException sqle) {
            GSQLogger.getInstance().writeError(sqle);
        }
        return false;
    }
}
