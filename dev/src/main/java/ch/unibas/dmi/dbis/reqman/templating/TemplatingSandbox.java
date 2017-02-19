package ch.unibas.dmi.dbis.reqman.templating;

import java.util.function.Function;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class TemplatingSandbox {

    public static void main(String[] args){

        Function<Integer, String> renderer = String::valueOf;

        Field<Person, Integer> strAge = new Field<>("age", Field.Type.NORMAL, Person::getAge, renderer);

        Function<Name, String> nameRenderer = name -> name.getFirst() + " " + name.getLast();

        Field<Person, Name> objName = new Field<>("name", Field.Type.OBJECT, Person::getName, nameRenderer);

        Person alice = new Person(new Name("Alice", "Smith"), 1);
        Person bob = new Person(new Name("Bob", "Miller"), 2);

        System.out.println(objName.render(alice)+" "+strAge.render(alice));
        System.out.println(objName.render(bob)+" "+strAge.render(bob));
    }

    private static class Person{

        private int age;

        private Name name;


        public Person(Name name, int age) {
            this.name = name;
            this.age = age;
        }

        public Name getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    public static class Name{
        private String first;
        private String last;

        public Name(String first, String last) {
            this.first = first;
            this.last = last;
        }

        public String getFirst() {
            return first;
        }

        public String getLast() {
            return last;
        }


    }
}
