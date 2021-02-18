package com.revature.ObjectMapper;

import com.revature.GSQLogger.GSQLogger;
import com.revature.META.MetaConstructor;
import com.revature.META.MetaModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;

public class ObjectGetter extends ObjectMapper{
    public static final ObjectGetter objCon = new ObjectGetter();

    private ObjectGetter() {
        super();
    }

    public static ObjectGetter getInstance() {
        return objCon;
    }

    private void setPreparedConditions(final PreparedStatement pstmt,final String[] conditions_split) {
        try {
            ParameterMetaData pd = pstmt.getParameterMetaData();
            int index = 1;
            for (String cond: conditions_split) {
                setPreparedStatementByType(pstmt,pd.getParameterTypeName(index),cond,index++);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Optional<List<Object>> queryDBForListObj(final Class<?> clazz,final String[] columns,final String[] conditions, final String[] operators,final Connection conn) {
       try {
           final MetaModel<?> model           = MetaConstructor.getInstance().getModels().get(clazz.getSimpleName());
           final String condition_str         = parseColumns(columns,operators);
           final String sql                   = "SELECT * FROM "  + model.getTable_name() + " WHERE " + condition_str;
           final PreparedStatement pstmt      = conn.prepareStatement(sql);
           setPreparedConditions(pstmt,conditions);
           final ResultSet rs = pstmt.executeQuery();
           final Optional<List<Object>> obj_list = getListObjFromResult(rs,model.getSetters(),model.getConstructor());
           addListToCache(obj_list);
           return obj_list;
       }catch (SQLException sqle) {
           GSQLogger.getInstance().writeError(sqle);
       }
        return Optional.empty();
    }

    public Optional<List<Object>> getListObjectFromDB(final Class<?> clazz, final String columns, final String conditions, final String operators, final Connection conn) {
            final MetaModel<?> model           = MetaConstructor.getInstance().getModels().get(clazz.getSimpleName());
            final String[] column_split        = columns.split(",");
            final String[] operator_split      = operators.split(",");
            final String[] condition_split     = conditions.split(",");
            final Optional<List<Object>> objs  = ObjectCache.getInstance().getObjFromCache(clazz,model.getGetters(),column_split,condition_split,operator_split);
            if(objs.isPresent()) {
                return objs;
            }
            return queryDBForListObj(clazz,column_split,condition_split,operator_split,conn);
    }

    protected void setFieldFromSetter(final Object obj, final Map.Entry<Method,String[]> setter, final ResultSet rs, final String type) {
        try {
            final Matcher match = pat.matcher(type);
            if(match.find()) {
                switch (match.group()) {
                    case "text":
                    case "String":
                    case "varchar":
                        setter.getKey().invoke(obj, rs.getString(setter.getValue()[0]));
                        break;
                    case "int":
                        setter.getKey().invoke(obj, rs.getInt(setter.getValue()[0]));
                        break;
                    case "double":
                        setter.getKey().invoke(obj, rs.getDouble(setter.getValue()[0]));
                        break;
                    case "float":
                        setter.getKey().invoke(obj, rs.getFloat(setter.getValue()[0]));
                        break;
                    case "timestamp":
                    case "timestamptz":
                        setter.getKey().invoke(obj,rs.getTimestamp(setter.getValue()[0]));
                        break;
                    default:
                        break;
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Optional<List<Object>> getListObjFromResult(final ResultSet rs, final HashMap<Method,String[]> setters, Constructor<?> constructor) {
        try {
            final List<Object> objs = new LinkedList<>();
            while(rs.next()) {
                final Object obj = constructor.newInstance();
                setters.entrySet().forEach(e -> setFieldFromSetter(obj,e,rs,e.getValue()[1]));
                objs.add(obj);
            }
            return (objs.size() > 0)? Optional.of(objs) : Optional.empty();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
