package com.revature.ObjectMapper;

import com.revature.Annotations.PrimaryKey;
import com.revature.GSQLogger.GSQLogger;
import com.revature.META.MetaConstructor;
import com.revature.META.MetaModel;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Class which handles removing objects from the database.
 */
public class ObjectRemover extends ObjectMapper{
    private static final ObjectRemover obj_remove = new ObjectRemover();


    private ObjectRemover() {
        super();
    }

    public static ObjectRemover getInstance() {
        return obj_remove;
    }

    /**
     * Gets string name of primary key.
     * @param clazz class to find primary key in.
     * @return String name of primary key.
     */
    private static String getPK(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).map(z -> z.getDeclaredAnnotation(PrimaryKey.class).name()).findFirst().get();
    }

    /**
     * Return the getter method for the primary key.
     * @param pk name of primary key in a particular.
     * @param getters HashMap of getters in a particular class.
     * @return Getter method for primary key.
     */
    private static Method getGetter(final String pk,final HashMap<String,Method> getters) {
        return getters.get(pk);

    }

    /**
     * Remove an object form the database.
     * @param obj object to remove from databse.
     * @param conn connection to the database.
     * @return boolean indicated success/failure of operation.
     */
    public boolean removeObjectFromDB(final Object obj, final Connection conn) {
        try {
            final MetaModel<?> model                = MetaConstructor.getInstance().getModels().get(obj.getClass().getSimpleName());
            final String primary_key                = getPK(obj.getClass());
            final Method getter                     = getGetter(primary_key,model.getGetters());
            final String sql                        = "DELETE from " + model.getTable_name() + " WHERE "+ primary_key + " = ? ";
            final PreparedStatement pstmt           = conn.prepareStatement(sql);
            final ParameterMetaData pd              = pstmt.getParameterMetaData();
            setStatement(pstmt, pd, getter, obj, 1);
            pstmt.executeUpdate();
            //also remove object from cache.
            ObjectCache.getInstance().removeObjFromCache(obj);
            return true;
        }catch(SQLException sqle) {
            GSQLogger.getInstance().writeError(sqle);
        }
        return false;
    }
}
