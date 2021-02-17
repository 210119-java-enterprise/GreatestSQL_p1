import Connection.ConnectionFactory;

import javax.xml.transform.Result;
import java.sql.*;

public class testTimestamp {

    public static void main(String[] args) throws SQLException {
        ConnectionFactory conn = ConnectionFactory.getInstance();
        final PreparedStatement pstmt = conn.getConnection().prepareStatement("SELECT * from tstry");
        final ResultSet rs = pstmt.executeQuery();
        final ParameterMetaData pd = pstmt.getParameterMetaData();
        final ResultSetMetaData rd = rs.getMetaData();
        System.out.println(rd.getColumnTypeName(1));
        System.out.println(rd.getColumnTypeName(2));
        System.out.println(rd.getColumnTypeName(3));

    }
}
