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

    public Object getValueFromDB(final MetaModel model, final String column) {

        return null;
    }

    public static ObjectSaver getInstance() {
        return objSaver;
    }

    private void setStatement(final PreparedStatement pstmt,final Map<Method,String> getters,final Object obj) {
        try {
            ParameterMetaData pd = pstmt.getParameterMetaData();
            int index = 1;
            for (Method m : getters.keySet()) {
                setPreparedStatementByType(pstmt, pd.getParameterTypeName(index),String.valueOf(m.invoke(obj)), index++);
            }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    public boolean saveObject(Object obj,final Connection conn) {
        try  {
            final MetaModel<?> model                = MetaConstructor.getInstance().getModels().get(obj.getClass().getSimpleName());
            final HashMap<Method,String> getters  = model.getGetters();
            final String args                       = getArgs(getters.keySet().size() - 1);
            final String columns                    = String.join(",",getters.values());
            final String sql                        = "INSERT INTO " + model.getTable_name() + " ( " + columns + " ) VALUES( " + args + " )";
            final PreparedStatement pstmt           = conn.prepareStatement(sql);
            setStatement(pstmt,getters,obj);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
