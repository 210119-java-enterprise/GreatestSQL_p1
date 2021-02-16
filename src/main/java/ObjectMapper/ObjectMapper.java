package ObjectMapper;

import Annotations.SerialKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.util.*;
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

    protected Optional<String> getSerialName(final Object obj) {
       return Arrays.stream(obj.getClass().getDeclaredFields())
                    .filter(f -> f.getDeclaredAnnotation(SerialKey.class) != null)
                    .map(f -> f.getDeclaredAnnotation(SerialKey.class).name())
                    .findFirst();
    }

    protected Optional<Map.Entry<Method,String[]>> getSerialKeyEntry(final Optional<String> name,final HashMap<Method,String[]> setters) {
        return setters.entrySet().stream()
                .filter(e -> e.getValue()[0].equals(name.orElse("null")))
                .findFirst();
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

    protected String parseColumns(final String columns, final String operators) {
        if(operators != null && !"".equals(operators.trim())) {
            final String[] columns_split = columns.split(",");
            final String[] operators_split = operators.split(",");
            final StringBuilder str = new StringBuilder();
            for (int i = 0; i < operators_split.length; i++) {
                str.append(columns_split[i]).append(" = ? ").append(operators_split[i]).append(" ");
            }
            str.append(columns_split[columns_split.length - 1]).append(" = ?");
            return str.toString();
        }
        return columns + " = ? ";
    }

}
