package ObjectMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;

import Annotations.Getter;
import Meta.MetaConstructor;
import Meta.MetaModel;
import Connection.ConnectionFactory;

public class ObjectGetter extends ObjectMapper{
    public static final ObjectGetter objCon = new ObjectGetter();

    private ObjectGetter() {
        super();
    }

    public static ObjectGetter getInstance() {
        return objCon;
    }

    private void setPreparedConditions(final PreparedStatement pstmt,final String conditions) {
        final String[] conditions_split = conditions.split(",");
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

    public List<Object> getListObjectFromDB(final Class<?> clazz,final String columns,final String conditions,final String operators,final Connection conn) {
        try {
            final MetaModel<?> model   = MetaConstructor.getInstance().getModels().get(clazz.getSimpleName());
            final String condition_str = parseColumns(columns,operators);
            final String sql = "SELECT * FROM "  + model.getTable_name() + " WHERE " + condition_str;
            final PreparedStatement pstmt = conn.prepareStatement(sql);
            setPreparedConditions(pstmt,conditions);
            final ResultSet rs = pstmt.executeQuery();
            return getListObjFromResult(rs,model.getSetters(),model.getConstructor());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
                    default:
                        break;
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private List<Object> getListObjFromResult(final ResultSet rs, final HashMap<Method,String[]> setters, Constructor<?> constructor) {
        try {
            final List<Object> objs = new LinkedList<>();
            while(rs.next()) {
                final Object obj = constructor.newInstance();
                setters.entrySet().forEach(e -> setFieldFromSetter(obj,e,rs,e.getValue()[1]));
                objs.add(obj);
            }
            return objs;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
