import com.revature.GSQL.GSQL;
import Models.Person;

import java.util.LinkedList;

public class TestObjectUpdater {
    public static void main(String[] args) {
        GSQL g = GSQL.getInstance();
        g.addClass(Person.class);
       // LinkedList<Object> p = (LinkedList<Object>) GSQL.getInstance().getListObjectFromDB(Person.class, "firstname", "chris");
       // Person me = (Person) p.get(0);
        //me.setFirstName("spaceghost");
       // g.UpdateObjectInDB(me,"firstname","id",String.valueOf(me.getId()),"");
         LinkedList<Object> p = (LinkedList<Object>) GSQL.getInstance().getListObjectFromDB(Person.class, "firstname", "chris").get();
         Person chad = (Person) p.get(0);
         chad.setFirstName("zorak");
         g.UpdateObjectInDB(chad,"firstname","id",String.valueOf(chad.getId()),"");

    }
}
