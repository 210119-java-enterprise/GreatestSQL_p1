package com.revature.ObjectMapper;

import com.revature.GSQLogger.GSQLogger;
import com.revature.META.MetaConstructor;
import com.revature.META.MetaModel;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ObjectUpdater extends ObjectMapper{
    private static final ObjectUpdater obj_updater = new ObjectUpdater();

    private ObjectUpdater() {
        super();
    }

   public static ObjectUpdater getInstance() {
        return obj_updater;
   }

   private void setUpdateStatement(final Object obj,final PreparedStatement pstmt,final HashMap<String,Method> getters,final ParameterMetaData pd,final String[] update_array,int index) {
        for(String s : update_array) {
            setStatement(pstmt,pd,getters.get(s),obj,index++);
        }
   }

   public boolean updateObject(final Object obj,final String update_columns, final String condition_columns,final String conditions,final String operators, final Connection conn) {
       try {
           final MetaModel<?> model               = MetaConstructor.getInstance().getModels().get(obj.getClass().getSimpleName());
           final HashMap<String,Method> getters  = model.getGetters();
           final String condition_str             = parseColumns(condition_columns.split(","), operators.split(","));
           final String[] update_array            = update_columns.split(",");
           final String new_columns               = String.join(" = ?, ", update_array) + " = ?";
           final String sql                       = "UPDATE " + model.getTable_name() + " SET " + new_columns + " where " + condition_str;
           final PreparedStatement pstmt          = conn.prepareStatement(sql);
           final ParameterMetaData pd             = pstmt.getParameterMetaData();
           setUpdateStatement(obj,pstmt,getters,pd,update_array,1);
           setUpdateStatement(obj,pstmt,getters,pd,condition_columns.split(","),update_array.length + 1);
           pstmt.executeUpdate();
           return true;
       } catch (Exception sqle) {
           GSQLogger.getInstance().writeError(sqle);
       }
       return false;
    }


}
