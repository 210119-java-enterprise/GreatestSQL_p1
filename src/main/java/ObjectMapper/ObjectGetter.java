package ObjectMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.*;
import java.util.*;
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

    private Method getGetter(final MetaModel<?> model,final String column) {
        for(Method getter : model.getGetters().keySet()) {
            if(getter.getDeclaredAnnotation(Getter.class).name().equals(column)) {
                return getter;
            }
        }
        return null;
    }

    private String parseColumns(final String columns, final String operators) {
        if(operators != null && !"".equals(operators.trim())) {
            final String[] columns_split = columns.split(",");
            final String[] operators_split = operators.split(",");
            System.out.println("operators split");
            Arrays.stream(operators_split).forEach(System.out::println);
            final StringBuilder str = new StringBuilder();
            for (int i = 0; i < operators_split.length; i++) {
                str.append(columns_split[i]).append(" = ? ").append(operators_split[i ]).append(" ");
            }
            str.append(columns_split[columns_split.length - 1]).append(" = ?");
            return str.toString();
        }
        return columns + " = ? ";
    }

    private void setPreparedConditions(final PreparedStatement pstmt,final String conditions) {
        final String[] conditions_split = conditions.split(",");
        try {
            ParameterMetaData pd = pstmt.getParameterMetaData();
            for (int i = 0; i < conditions_split.length; i++) {
                System.out.println("condition is: "+ conditions_split[i]);
                setPreparedStatementByTpe(pstmt,pd.getParameterTypeName(i+1),conditions_split[i],i+1);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public List<Object> getObjectFromDB(Class<?> clazz,final String columns,final String conditions,final String operators,final Connection conn) {
        try {
            final MetaModel<?> model   = MetaConstructor.getInstance().getModels().get(clazz.getSimpleName());
            final String condition_str = parseColumns(columns,operators);
            final String sql = "SELECT * FROM "  + model.getTable_name() + " WHERE " + condition_str;
            System.out.println(sql);
            final PreparedStatement pstmt = conn.prepareStatement(sql);
            setPreparedConditions(pstmt,conditions);
            final ResultSet rs = pstmt.executeQuery();
            return getObjFromResult(rs,model.getSetters(),model.getConstructor());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void setFieldFromSetter(final Object obj, final Map.Entry<Method,String[]> setter, final ResultSet rs) {
        try {
            switch (setter.getValue()[1]) {
                case "String":
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
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private List<Object> getObjFromResult(final ResultSet rs, final HashMap<Method,String[]> setters, Constructor<?> constructor) {
        try {
            List<Object> objs = new LinkedList<>();
            while(rs.next()) {
                Object obj = constructor.newInstance();
                setters.entrySet().forEach(e -> setFieldFromSetter(obj,e,rs));
                objs.add(obj);
            }
            return objs;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
