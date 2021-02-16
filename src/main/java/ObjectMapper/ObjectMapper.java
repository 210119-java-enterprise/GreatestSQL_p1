package ObjectMapper;

import java.lang.reflect.Method;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ObjectMapper {
    protected Pattern pat = Pattern.compile("[^\\d]+");

    protected String getArgs(final int length) {
        return String.join(",", Collections.nCopies(length,"?")) + ",? ";
    }

    protected void setStatement(final PreparedStatement pstmt, final ParameterMetaData pd, final Method getter, final Object obj, final int index) {
        try {
            setPreparedStatementByType(pstmt, pd.getParameterTypeName(index),String.valueOf(getter.invoke(obj)), index);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void setPreparedStatementByType(final PreparedStatement pstmt, final String type,final String input,final int index) {
        try {
            Matcher match = pat.matcher(type);
            if (match.find()) {
                switch (match.group()) {
                    case "text":
                    case "String":
                    case "varchar":
                        pstmt.setString(index, input);
                        break;
                    case "int":
                        pstmt.setInt(index, Integer.parseInt(input));
                        break;
                    case "float":
                        pstmt.setFloat(index, Float.parseFloat(input));
                        break;
                    case "double":
                        pstmt.setDouble(index, Double.parseDouble(input));
                        break;
                    default:
                        break;
                }
            }
      }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
