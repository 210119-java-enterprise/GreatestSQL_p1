import GSQL.GSQL;
import Models.Person;

public class TestObjectSaver {
    public static void main(String[] args) {
        GSQL.getInstance().addClass(Person.class);
        Person p = new Person(21,"chris","nopenope");
        GSQL.getInstance().addObjectToDB(p);
        System.out.println(p.toString());
    }


}
