import Models.Person;
import com.revature.GSQL.GSQL;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class TestGettingObj {
    final GSQL g   = GSQL.getInstance();

    @Test
    public void test_WrongUsername() {
        g.addClass(Person.class);
        assertTrue(g.getListObjectFromDB(Person.class,"firstname","jill").size() == 0);
    }

    @Test
    public void test_CorrectUserName() {
        g.addClass(Person.class);
        final List<Object> l = g.getListObjectFromDB(Person.class,"firstname","chris");
        assertTrue(l.size() > 0);
        assertEquals("chris",((Person)l.get(0)).getFirstName());
    }

    @Test
    public void test_mutlipleConditions() {
        g.addClass(Person.class);
        final List<Object> l = g.getListObjectFromDB(Person.class,"firstname,lastname","chris,none","AND");
        assertTrue(l.size() > 0);
        assertEquals("chris",((Person)l.get(0)).getFirstName());
        assertEquals("none",((Person)l.get(0)).getLastName());
    }
}
