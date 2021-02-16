package ObjectMapper;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
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
        System.out.println(serial_name.get());
        return String.join(",",getters.stream()
                    .filter(s -> (!serial_name.isPresent() || !s.equals(serial_name.get())))
                    .toArray(String[]::new));
    }

    public boolean saveObject(final Object obj,final Connection conn) {
        try  {
            final MetaModel<?> model                          = MetaConstructor.getInstance().getModels().get(obj.getClass().getSimpleName());
            final HashMap<Method,String> getters              = model.getGetters();
            final Optional<String> serial_name                = getSerialName(obj);
            final Optional<Map.Entry<Method,String[]>> setter = getSerialKeyEntry(serial_name,model.getSetters());
            final String args                                 = getArgs( (serial_name.isPresent())? getters.keySet().size()- 2 : getters.keySet().size()- 1);
            final String columns                              = getColumns(getters.values(),serial_name);
            final String sql                                  = "INSERT INTO " + model.getTable_name() + " ( " + columns + " ) VALUES( " + args + " )";
            System.out.println(sql);
            final PreparedStatement pstmt                     = conn.prepareStatement(sql);
            final ParameterMetaData pd                        = pstmt.getParameterMetaData();
            int index                                         = 1;
            for(Map.Entry<Method,String> getter : getters.entrySet()) {
                if(!serial_name.isPresent() || !getter.getValue().equals(serial_name.get())) {
                    System.out.println("name is: " + getter.getValue());
                    setStatement(pstmt, pd, getter.getKey(), obj, index++);
                }
            }
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
