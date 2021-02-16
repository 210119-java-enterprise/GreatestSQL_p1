import Annotations.Column;
import Annotations.Getter;
import Annotations.PrimaryKey;
import GSQL.GSQL;
import Meta.MetaConstructor;
import Meta.MetaModel;
import Models.Person;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestObjRemover {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        final GSQL g = GSQL.getInstance();
        Object obj = new Person();
        g.addClass(Person.class);
        final MetaModel<?> model  = MetaConstructor.getInstance().getModels().get(obj.getClass().getSimpleName());
        Person p = (Person) g.getListObjectFromDB(Person.class, "firstname,lastname", "no,name", "AND").get(0);
        System.out.println(p.toString());
        g.removeObjectFromDB(p);
        String name = Arrays.stream(obj.getClass().getDeclaredFields()).filter(f -> f.getDeclaredAnnotation(PrimaryKey.class) != null).map(z -> z.getDeclaredAnnotation(PrimaryKey.class).name()).findAny().get();
        System.out.println(name);
        HashMap<Method, String> hg = MetaConstructor.getInstance().getModels().get(Person.class.getSimpleName()).getGetters();
        Method m = hg.entrySet().stream().filter(s -> s.getValue().equals(name)).map(Map.Entry::getKey).findFirst().get();
        System.out.println(m.invoke(p));
    }
}
