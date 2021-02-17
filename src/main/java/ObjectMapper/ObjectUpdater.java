package ObjectMapper;

import Logger.GSQLogger;
import Meta.MetaConstructor;
import Meta.MetaModel;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ObjectUpdater extends ObjectMapper{
    private static ObjectUpdater obj_updater = new ObjectUpdater();

    private ObjectUpdater() {
        super();
    }

   public static ObjectUpdater getInstance() {
        return obj_updater;
   }

   private void setUpdateStatement(final Object obj,final PreparedStatement pstmt,final HashMap<Method,String> getters,final ParameterMetaData pd,final String[] update_array,int index) {
        for(String s : update_array) {
            final Map.Entry<Method,String> getter = getters.entrySet().stream().filter(e -> e.getValue().equals(s)).findFirst().get();
            setStatement(pstmt,pd,getter.getKey(),obj,index++);
        }
   }

   public boolean updateObject(final Object obj,final String update_columns, final String condition_columns,final String conditions,final String operators, final Connection conn) {
       try {
           final MetaModel<?> model               = MetaConstructor.getInstance().getModels().get(obj.getClass().getSimpleName());
           final HashMap<Method, String> getters  = model.getGetters();
           final String condition_str             = parseColumns(condition_columns, operators);
           final String[] update_array            = update_columns.split(",");
           final String new_columns               = String.join(" = ?, ", update_array) + " = ?";
           final String sql                       = "UPDATE " + model.getTable_name() + " SET " + new_columns + " where " + condition_str;
           final PreparedStatement pstmt          = conn.prepareStatement(sql);
           final ParameterMetaData pd             = pstmt.getParameterMetaData();
           setUpdateStatement(obj,pstmt,getters,pd,update_array,1);
           setUpdateStatement(obj,pstmt,getters,pd,condition_columns.split(","),update_array.length + 1);
           pstmt.executeUpdate();
           return true;
       } catch (SQLException sqle) {
           GSQLogger.getInstance().writeError(sqle);
       }
       return false;
    }


}
