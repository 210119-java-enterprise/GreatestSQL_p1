import GSQL.GSQL;
import Models.Person;

public class TestObjectSaver {
    public static void main(String[] args) {
        GSQL.getInstance().addClass(Person.class);
        Person p = new Person(10,"none","yep");
        GSQL.getInstance().addObjectToDB(p);
    }


}
