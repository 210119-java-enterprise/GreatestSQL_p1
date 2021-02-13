package ObjectMapper;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ObjectMapper {
    private Pattern pat = Pattern.compile("[^0-9]+");

    protected String getArgs(final int length) {
        return String.join(",", Collections.nCopies(length,"?")) + ",? ";
    }

    protected void setPreparedStatementByTpe(final PreparedStatement pstmt, final String type,final String input,final int index) {
        try {
            Matcher match = pat.matcher(type);
            if (match.find()) {
                switch (match.group()) {
                    case "text":
                    case "String":
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
