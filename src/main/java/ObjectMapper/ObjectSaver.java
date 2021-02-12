package ObjectMapper;

import Meta.MetaConstructor;
import Meta.MetaModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public boolean saveClass(Object obj) throws InvocationTargetException, IllegalAccessException {
        try (Connection conn = ConnectionFactory.getInstance().getConnection()) {
            final MetaModel<?> model = MetaConstructor.getInstance().getModels().get(obj.getClass().getSimpleName());
            final Method[] getters   = model.getGetters();
            final String args        = getArgs(getters.length - 1);
            final String columns     = getGetterColumnNames(getters);
            final String sql = "INSERT INTO " + model.getTable_name() + " ( " + columns + " ) VALUES( " + args + " )";
            final PreparedStatement pstmt = conn.prepareStatement(sql);
            for(int i = 0; i < getters.length;i++) {
                setStatement(pstmt, getters[i],getters[i].getReturnType().getSimpleName(),1 + i,obj);
            }
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
