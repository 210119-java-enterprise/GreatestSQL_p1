import GSQL.GSQL;
import Models.Person;

import java.util.LinkedList;

public class TestTransactions {
    public static void main(String[] args) {
        GSQL.getInstance().addClass(Person.class);
       GSQL.getInstance().setTransaction();
       // GSQL.getInstance().beginCommit();
        GSQL.getInstance().setSavepoint("sp1");
        final Person me = new Person(6,"chris","nichols");
        GSQL.getInstance().addObjectToDB(me);
        LinkedList<Object> p = (LinkedList<Object>) GSQL.getInstance().getListObjectFromDB(Person.class, "firstname", "chris");
        for (Object pers : p) {
            System.out.println(pers.toString());
        }
        GSQL.getInstance().Rollback("sp1");
        System.out.println("rollback");
        LinkedList<Object> g = (LinkedList<Object>) GSQL.getInstance().getListObjectFromDB(Person.class, "firstname", "chris");
        for (Object pers : g) {
            System.out.println(pers.toString());
        }
    }
}
