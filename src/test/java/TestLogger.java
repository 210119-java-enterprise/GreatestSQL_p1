import com.revature.GSQL.GSQL;
import com.revature.GSQLogger.GSQLogger;
import Models.Person;

public class TestLogger {
    public static void main(String[] args) {
        final GSQL g = GSQL.getInstance();
        GSQLogger.getInstance().writeError("this is a message");
        final Person p = new Person(100,"billy","bob");
        g.UpdateObjectInDB(p,"firstname,lastname");
    }
}
