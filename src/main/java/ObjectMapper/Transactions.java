package ObjectMapper;

import Connection.ConnectionFactory;
import Logger.GSQLogger;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Transactions {

    private static final Transactions trans = new Transactions();

    private Transactions(){
        super();
    }

    public static Transactions getTransaction() {
        return trans;
    }


    private void applyTransaction(final String sql) {
        try {
            final PreparedStatement pstmt = ConnectionFactory.getInstance().getConnection().prepareStatement(sql);
            pstmt.executeUpdate();
        }catch(SQLException sqle) {
            GSQLogger.getInstance().writeError(sqle);
        }
    }

    public void Commit() {
        applyTransaction("COMMIT");
    }

    public void Rollback() {
        applyTransaction("ROLLBACK");
    }
    public void Rollback(final String name) {
       applyTransaction("ROLLBACK TO " + name);
    }

    public void Savepoint(final String name) {
        applyTransaction("SAVEPOINT " + name);
    }

    public void ReleaseSavepoint(final String name) {
       applyTransaction("RELEASE SAVEPOINT " + name);
    }

}
