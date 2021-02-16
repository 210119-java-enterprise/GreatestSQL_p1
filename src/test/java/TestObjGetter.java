import GSQL.GSQL;
import ObjectMapper.ObjectGetter;
import Models.Person;
import Connection.ConnectionFactory;
import java.sql.Connection;
import java.util.LinkedList;

public class TestObjGetter {
    public static void main(String[] args) {
        GSQL.getInstance().addClass(Person.class);

        LinkedList<Object> p = (LinkedList<Object>) GSQL.getInstance().getListObjectFromDB(Person.class,"firstname","chris");
        for(Object pers: p) {
            System.out.println(pers.toString());
        }
        System.out.println("second test");
        LinkedList<Object> g = (LinkedList<Object>) GSQL.getInstance().getListObjectFromDB(Person.class,"id,firstname,lastname","4,chris,nope","AND,AND");
        for(Object gers: g) {
            System.out.println(gers.toString());
        }
        System.out.println("third test");
        LinkedList<Object> y = (LinkedList<Object>) GSQL.getInstance().getListObjectFromDB(Person.class,"firstname,lastname","no,name","AND");
        for(Object gers: y) {
            System.out.println(gers.toString());
        }
    }

}
