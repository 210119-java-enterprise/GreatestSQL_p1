import GSQL.GSQL;
import ObjectMapper.ObjectGetter;
import Models.Person;
import Connection.ConnectionFactory;
import java.sql.Connection;
import java.util.LinkedList;

public class TestObjGetter {
    public static void main(String[] args) {
        GSQL.getInstance().addClass(Person.class);
        final Connection conn = ConnectionFactory.getInstance().getConnection();

        LinkedList<Object> p = (LinkedList<Object>) ObjectGetter.getInstance().getObjectFromDB(Person.class,"firstname","chris",null,conn);
        for(Object pers: p) {
            System.out.println(pers.toString());
        }
        LinkedList<Object> g = (LinkedList<Object>) ObjectGetter.getInstance().getObjectFromDB(Person.class,"id,firstname,lastname","4,chris,nope","AND,AND",conn);
        for(Object gers: g) {
            System.out.println(gers.toString());
        }
    }

}
