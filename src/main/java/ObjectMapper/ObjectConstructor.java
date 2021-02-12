package ObjectMapper;

import Annotations.Getter;
import Meta.MetaConstructor;
import Meta.MetaModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ObjectConstructor extends ObjectMapper{
    public static final ObjectConstructor objCon = new ObjectConstructor();

    private ObjectConstructor() {
        super();
    }

    public ObjectConstructor getInstance() {
        return objCon;
    }

    private Method getGetter(final MetaModel<?> model,final String column) {
        for(Method getter : model.getGetters().keySet()) {
            if(getter.getDeclaredAnnotation(Getter.class).name().equals(column)) {
                return getter;
            }
            return null;
        }
    }

    public List<Object> getObjectFromDB(Class<?> clazz,final String column,final String condition) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        try (Connection conn = ConnectionFactory.getInstance().getConnection()) {
            final MetaModel<?> model = MetaConstructor.getInstance().getModels().get(clazz.getSimpleName());
            final String sql = "SELECT * FROM "  + model.getTable_name() + " WHERE " + column + " = ?";
            final PreparedStatement pstmt = conn.prepareStatement(sql);
            final Method getter           = getGetter(model,column);
            pstmt.setString(1,condition);
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
