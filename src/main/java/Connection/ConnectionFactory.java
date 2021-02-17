package Connection;

import Logger.GSQLogger;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Class which represents a connection to the application database.
 * Only a single instance of class is available during use of application.
 */
public class ConnectionFactory {
    private static final ConnectionFactory connection_factory = new ConnectionFactory();
    private final Properties props = new Properties();
    static {
        try {
            Class.forName("org.postgresql.Driver");
        }catch (ClassNotFoundException cnfe) {
            GSQLogger.getInstance().writeError(cnfe);
        }
    }

    /**
     * private constructor for Utils.ConnectionFactory class.
     */
    private ConnectionFactory() {
        try {
            props.load(new FileReader("src/main/resources/application.properties"));
        }catch(IOException ioe) {
            GSQLogger.getInstance().writeError(ioe);
        }
    }

    /**
     * Method to retrieve current static instance of Utils.ConnectionFactory class.
     * @return current instance of Utils.ConnectionFactory object.
     */
    public static ConnectionFactory getInstance() {
        return connection_factory;
    }

    /**
     * Method to create a connection to application database.
     * @return Connection object.
     */
    public Connection getConnection () {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    props.getProperty("url"),
                    props.getProperty("admin-usr"),
                    props.getProperty("admin-pw")
            );
        }catch (SQLException sqle) {
            GSQLogger.getInstance().writeError(sqle);
        }
        return conn;
    }
}
