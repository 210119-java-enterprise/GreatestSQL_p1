package Models;

import Annotations.*;

import java.util.Objects;
@Table(name = "Users")
public class Person {
    @Column(name = "id")
    @PrimaryKey(name = "id")
    private int id;

    @Column(name = "firstname")
    private String first_name;

    @Column(name = "lastname")
    private String last_name;

    @SerialKey(name = "sk")
    private int pk;

    public Person() {
        super();
    }

    public Person(String first_name,String last_name) {
        this.first_name = first_name;
        this.last_name = last_name;

    }

    public Person(final int id, final String first_name, final String last_name) {
        this(first_name,last_name);
        this.id = id;
        this.pk = id + 5;
    }

    @Getter(name = "sk")
    public int getPk() {
        return pk;
    }
    @Setter(name = "sk")
    public void setPk(int pk) {
        this.pk = pk;
    }

    @Getter(name = "id")
    public int getId() {
        return id;
    }

    @Setter(name = "id")
    public void setId(int id) {
        this.id = id;
    }

    @Getter(name = "firstname")
    public String getFirstName() {
        return first_name;
    }

    @Setter(name = "firstname")
    public void setFirstName( String first_name) {
        this.first_name = first_name;
    }

    @Getter(name = "lastname")
    public String getLastName() {
        return last_name;
    }

    @Setter(name = "lastname")
    public void setLastName( String last_name) {
        this.last_name = last_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && first_name.equals(person.first_name) && last_name.equals(person.last_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, first_name, last_name);
    }

    @Override
    public String toString() {
        return "Model.Person{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                '}';
    }
}
