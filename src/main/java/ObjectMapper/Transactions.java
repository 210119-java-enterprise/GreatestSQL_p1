package ObjectMapper;

import Connection.ConnectionFactory;
import Logger.GSQLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;

public class Transactions {

    private static final Transactions trans = new Transactions();
    private final HashMap<String,Savepoint> savepoints;

    private Transactions(){
        super();
        savepoints = new HashMap<String,Savepoint>();
    }

    public static Transactions getTransaction() {
        return trans;
    }

    public void enableAutoCommit(final Connection conn) {
        try {
           conn.setAutoCommit(true);
        }catch(SQLException sqle) {
            GSQLogger.getInstance().writeError(sqle);
        }
    }

    public void Commit(final Connection conn) {
        try {
            conn.setAutoCommit(false);
            conn.commit();
        }catch (SQLException sqle) {
            GSQLogger.getInstance().writeError(sqle);
        }
    }

    public void Rollback(final Connection conn) {
       try {
        conn.rollback();
       }catch(SQLException sqle) {
           GSQLogger.getInstance().writeError(sqle);
       }
    }

    public void Rollback(final String name,final Connection conn) {
        try {
            if(savepoints.containsKey(name)) {
                conn.rollback(savepoints.get(name));
            }
            else {
                GSQLogger.getInstance().writeError("tried to access a non-existent savepoint");
            }
        }catch(SQLException sqle) {
            GSQLogger.getInstance().writeError(sqle);
        }
    }

    public void Savepoint(final String name,final Connection conn) {
        try {
            final Savepoint save = conn.setSavepoint(name);
            savepoints.put(name, save);
        } catch (SQLException sqle) {
            GSQLogger.getInstance().writeError(sqle);
        }
    }

    public void ReleaseSavepoint(final String name,final Connection conn) {
        try {
            if (savepoints.containsKey(name)) {
                conn.releaseSavepoint(savepoints.get(name));
            } else {
                GSQLogger.getInstance().writeError("tried to access a non-existent savepoint");
            }
        } catch (SQLException sqle) {
            GSQLogger.getInstance().writeError(sqle);
        }
    }
}
