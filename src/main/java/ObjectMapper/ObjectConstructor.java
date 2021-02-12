package ObjectMapper;

import Meta.MetaConstructor;
import Meta.MetaModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ObjectConstructor extends ObjectMapper{
    public static final ObjectConstructor objCon = new ObjectConstructor();

    private ObjectConstructor() {
        super();
    }

    public ObjectConstructor getInstance() {
        return objCon;
    }

    public List<Object> getObjectFromDB(Class<?> clazz,final String column,final String condition) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        try (Connection conn = ConnectionFactory.getInstance().getConnection()) {
            final MetaModel<?> model = MetaConstructor.getInstance().getModels().get(clazz.getSimpleName());
            final String sql = "SELECT * FROM "  + model.getTable_name() + " WHERE " + column + " = ?";
            final PreparedStatement pstmt = conn.prepareStatement(sql);
            final Method getter           = Arrays.stream(model.getGetters())
                    .filter(i-> i.getDeclaredAnnotation(Getter.class).name().equals(column))
                    .toArray(Method[]::new)[0];
            pstmt.setString(1,condition);
            final ResultSet rs = pstmt.executeQuery();
            return getObjFromResult(rs,model.getSetters(),model.getConstructor());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Object> getObjFromResult(final ResultSet rs, final Method[] setters, Constructor<?> constructor) {
        try {
            List<Object> objs = new LinkedList<>();
            while(rs.next()) {
                Object obj = constructor.newInstance();
                Arrays.stream(setters).forEach(setter -> setFieldFromSetter(obj,setter,rs));
                objs.add(obj);
            }
            return objs;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
