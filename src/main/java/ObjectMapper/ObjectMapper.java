package ObjectMapper;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import Annotations.Column;
import Annotations.Getter;
import Annotations.Setter;
import Annotations.Table;

public abstract class ObjectMapper {

    protected String getArgs(final int length) {
        return String.join(",", Collections.nCopies(length,"?")) + ",? ";
    }

    protected void setFieldFromSetter(final Object obj,final Method setter, final ResultSet rs) {
        try {
            final String column = setter.getDeclaredAnnotation(Setter.class).name();
            switch (setter.getParameterTypes()[0].getSimpleName()) {
                case "String":
                    setter.invoke(obj, rs.getString(column));
                    break;
                case "int":
                    setter.invoke(obj, rs.getInt(column));
                    break;
                case "double":
                    setter.invoke(obj, rs.getDouble(column));
                    break;
                case "float":
                    setter.invoke(obj, rs.getFloat(column));
                    break;
                default:
                    break;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected void setStatement(final PreparedStatement pstmt, final Method method, final String typeName, final int index, final Object obj) {
        try {
            switch (typeName) {
                case "String":
                    pstmt.setString(index, (String) method.invoke(obj));
                    break;
                case "int":
                    pstmt.setInt(index, (int)method.invoke(obj));
                    break;
                case "float":
                    pstmt.setFloat(index, (float)method.invoke(obj));
                    break;
                case "double":
                    pstmt.setDouble(index, (double)method.invoke(obj));
                    break;
                default:
                    break;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
