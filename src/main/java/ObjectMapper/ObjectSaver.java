package ObjectMapper;

import Meta.MetaConstructor;
import Meta.MetaModel;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class ObjectSaver extends ObjectMapper{
    public static final ObjectSaver objSaver = new ObjectSaver();

    private ObjectSaver() {
        super();
    }

    public static Object getValueFromDB(final MetaModel model, final String column) {

        return null;
    }

    public ObjectSaver getInstance() {
        return objSaver;
    }

    private String getGetterColumnNames(final Collection<String[]> entities) {
        final List<String> columns = new LinkedList<>();
        entities.stream().map(i -> i[0]).forEach(columns::add);
        return String.join(",",columns);
    }

    private void setStatement(final PreparedStatement pstmt,final Map.Entry<Method,String[]> getter, final int index, final Object obj) {
        try {

            switch (getter.getValue()[1]) {
                case "String":
                    pstmt.setString(index, (String) getter.getKey().invoke(obj));
                    break;
                case "int":
                    pstmt.setInt(index, (int)getter.getKey().invoke(obj));
                    break;
                case "float":
                    pstmt.setFloat(index, (float)getter.getKey().invoke(obj));
                    break;
                case "double":
                    pstmt.setDouble(index, (double)getter.getKey().invoke(obj));
                    break;
                default:
                    break;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveObject(Object obj) {
        try (Connection conn = ConnectionFactory.getInstance().getConnection()) {
            final MetaModel<?> model                = MetaConstructor.getInstance().getModels().get(obj.getClass().getSimpleName());
            final HashMap<Method,String[]> getters  = model.getGetters();
            final String args                       = getArgs(getters.keySet().size() - 1);
            final String columns                    = getGetterColumnNames(getters.values());
            final String sql                        = "INSERT INTO " + model.getTable_name() + " ( " + columns + " ) VALUES( " + args + " )";
            final PreparedStatement pstmt           = conn.prepareStatement(sql);
            int index = 1;
            for(Map.Entry<Method,String[]> e: getters.entrySet()) {
                setStatement(pstmt, e, index++, obj);
            }
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
