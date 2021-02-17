package ObjectMapper;

import Annotations.PrimaryKey;
import Logger.GSQLogger;
import Meta.MetaConstructor;
import Meta.MetaModel;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ObjectRemover extends ObjectMapper{
    private static ObjectRemover obj_remove = new ObjectRemover();


    private ObjectRemover() {
        super();
    }

    public static ObjectRemover getInstance() {
        return obj_remove;
    }

    private static String getPK(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).map(z -> z.getDeclaredAnnotation(PrimaryKey.class).name()).findFirst().get();
    }

    private static Method getGetter(final String pk,final HashMap<Method,String> getters) {
        return getters.entrySet().stream()
                .filter(m -> m.getValue().equals(pk))
                .map(Map.Entry::getKey)
                .findFirst().get();
    }

    public boolean removeObjectFromDB(final Object obj, final Connection conn) {
        try {
            final MetaModel<?> model                = MetaConstructor.getInstance().getModels().get(obj.getClass().getSimpleName());
            final String primary_key                = getPK(obj.getClass());
            final Method getter                     = getGetter(primary_key,model.getGetters());
            final String sql                        = "DELETE from " + model.getTable_name() + " WHERE "+ primary_key + " = ? ";
            System.out.println(sql);
            final PreparedStatement pstmt           = conn.prepareStatement(sql);
            final ParameterMetaData pd              = pstmt.getParameterMetaData();
            setStatement(pstmt, pd, getter, obj, 1);
            pstmt.executeUpdate();
            return true;
        }catch(SQLException sqle) {
            GSQLogger.getInstance().writeError(sqle);
        }
        return false;
    }
}
