package ch.unibas.dmi.dbis.reqman.templating;

import java.util.function.Function;

/**
 * The class {@link Field} represents a field (property, attribute) of the related entity.
 * This class is used to define renderer / to render fields in a later stage.
 * <p>
 * <p>
 * <p>
 * <h2>Basic Example</h2>
 * Given the following class:
 * <pre>class Person{
 *     private String name;
 *     private int age;
 *
 *     public Person(String name, int age){
 *         this.name = name;
 *         this.age = age;
 *     }
 *
 *     public String getName(){
 *         return name;
 *     }
 *
 *     public int getAge(){
 *         return age;
 *     }
 *
 * }</pre>
 * <p>
 * The class could be seen as an entity with the following fields:
 * <pre>
 *     Field<Person, String> nameField = new Filed<>("name", Field.Type.RAW, Field::getName);
 *     Field<Person, Integer> ageField = new Field<>("age", Field.Type.RAW, Field::getAge);
 * </pre>
 * <p>
 * <h2>Example for a field with type OBJECT</h2>
 * The above example class is modified and looks now as follows:
 * <pre>class Person{
 *     private Name name;
 *     private int age;
 *
 *     public Person(Name name, int age){
 *         this.name = name;
 *         this.age = age;
 *     }
 *
 *     public Name getName(){
 *         return name;
 *     }
 *
 *     public int getAge(){
 *         return age;
 *     }
 *
 *     public static class Name{
 *          private String first;
 *          private String last;
 *
 *          public Name(String first, String last) {
 *              this.first = first;
 *              this.last = last;
 *          }
 *
 *          public String getFirst() {
 *              return first;
 *          }
 *
 *          public String getLast() {
 *              return last;
 *          }
 * }
 *
 * }</pre>
 * <p>
 * Since the attribute {@code name} of the class {@code Person} is an instance of {@code Name} on its own, one must provide a {@code renderer}:
 * <pre>
 *     Function<Name, String> nameRenderer = name -> name.getFirst() + " " + name.getLast();
 *     Field<Person, Name> nameField = new Field<>("name", Field.Type.OBJECT, Person::getName, nameRenderer);
 * </pre>
 * If no {@code renderer} would have been provided, an {@link IllegalArgumentException} would have been thrown.
 *
 * @param <E> The type of the entity this field belongs to
 * @param <T> The type of the field this {@link Field} represents.
 * @author loris.sauter
 */
public class Field<E, T> {

    /**
     * The name of the field, does not have to match the name of the representing field
     */
    private String name;
    /**
     * The field's type
     */
    private Type type;
    /**
     * The getter for the field
     */
    private Function<E, T> getter;
    /**
     * The renderer for the field, if the field's type is {@link Type#OBJECT}
     */
    private Function<T, String> renderer;
    private Entity<T> subEntity = null;

    /**
     * Constructor for a new {@link Field} with specified properties.
     * <p>
     * This constructor is intended to when instantiating fields with either type {@link Type#RAW} or {@link Type#ENTITY}
     * if the type would be {@link Type#OBJECT}, an {@link IllegalArgumentException} will be thrown. Use {@link Field#Field(String, Type, Function, Function, Entity)} instead.
     *
     * @param name   The name of the field
     * @param type   The field's type.
     * @param getter The getter used to get the field's value.
     * @throws IllegalArgumentException If the type is {@link Type#OBJECT}
     */
    public Field(String name, Type type, Function<E, T> getter) throws IllegalArgumentException {
        if (type == Type.OBJECT) {
            throw new IllegalArgumentException("If this field's type is Type.OBJECT, an appropriate *renderer must be provided*. See the docs for more information");
        }
        this.name = name;
        this.type = type;
        this.getter = getter;

    }

    /**
     * Constructs a new {@link Field} with specified properties and with a renderer provided.
     * <p>
     * This constructor is intended to use if the field's type is of type {@link Type#OBJECT}.
     * The field then must know how to render itself and thus needs to have a {@code renderer} specified.
     * <p>
     * A a {@code renderer} is any arbitrary {@link Function} which returns a {@link String} when given an object
     * of this field's type. Refer to the classes introduction to understand why and how a renderer is provided.
     *
     * @param name     The name of the field
     * @param type     The field's type
     * @param getter   The getter used to get the field's value
     * @param renderer The renderer which renders this field.
     */
    public Field(String name, Type type, Function<E, T> getter, Function<T, String> renderer) {
        this.name = name;
        this.type = type;
        this.getter = getter;
        this.renderer = renderer;
    }

    // TODO Write factory methods for all types!

    /**
     *
     * @param name
     * @param type
     * @param getter
     * @param subEntity The sub entity.
     */
    public Field(String name, Type type, Function<E, T> getter, Entity<T> subEntity) {
        this.name = name;
        this.type = type;
        this.getter = getter;
        this.subEntity = subEntity;
    }

    private String subFieldName;
    public String getSubFieldName(){
        return subFieldName;
    }
    public void setSubFieldName(String name){
        subFieldName = name;
    }

    /**
     * Renders the instance's field.
     * Be aware that this method returns null if the type is {@link Type#ENTITY}
     *
     * @param instance The instance from where the getter gets its resulting value.
     * @return Either the rendered field or null, if the type is {@link Type#ENTITY}
     */
    public String render(E instance) {
        T value = getter.apply(instance);
        switch (type) {
            case RAW:
                return String.valueOf(value);
            case OBJECT:
                return renderer.apply(value);
            case ENTITY:
            default:
                return null;
        }
    }

    /**
     * Checks if the given object equals this instance.
     * <p>
     * <i>IntelliJ generated</i>
     *
     * @param obj The other object to compare on equality.
     * @return true if the given object is the same as this instance
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Field<?, ?> field = (Field<?, ?>) obj;

        if (name != null ? !name.equals(field.name) : field.name != null) {
            return false;
        }
        if (type != field.type) {
            return false;
        }
        return getter != null ? getter.equals(field.getter) : field.getter == null;
    }

    /**
     * Returns the hashCode of this instance.
     * <p>
     * <i>IntelliJ generated</i>
     *
     * @return The hash code of this instance.
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (getter != null ? getter.hashCode() : 0);
        return result;
    }

    /**
     * Returns the sub entity if this {@link Field } is of {@link Type#ENTITY}.
     *
     * @return
     */
    public Entity<T> getSubEntity() {
        return subEntity;
    }

    /**
     * Returns the name of the field.
     *
     * @return The name of the field
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the field.
     *
     * @param name The name of this field.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the type of this field.
     *
     * @return The type of this field
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of this field
     *
     * @param type The type of this field
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the getter, used to get the value of the field this field is representing.
     *
     * @return The getter, as a {@link Function}
     */
    public Function<E, T> getGetter() {
        return getter;
    }

    /**
     * Sets the getter
     *
     * @param getter The getter as a {@link Function}
     */
    public void setGetter(Function<E, T> getter) {
        this.getter = getter;
    }

    /**
     * Returns a string representation of this field.
     * <p>
     * <i>IntelliJ generated</i>
     *
     * @return Returns a string representation of this field.
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Field{");
        sb.append("name='").append(name).append('\'');
        sb.append(", type=").append(type);
        sb.append(", getter=").append(getter);
        sb.append(", renderer=").append(renderer);
        sb.append('}');
        return sb.toString();
    }

    /**
     * The enumeration {@link Type} is used to differentiate between different types of {@link Field}s.
     */
    public static enum Type {
        /**
         * States that the {@link Field} with this type represents a field who's type is a raw data type.
         * <p>
         * Some raw data types are:
         * <ul>
         * <li>String</li><li>boolean</li><li>int</li><li>double</li><li>...</li>
         * </ul>
         * <p>
         * An arbitrary class is not a raw type.
         */
        RAW,
        /**
         * States that the {@link Field} with this type represents a field who's type is any arbitrary class.
         */
        OBJECT,
        /**
         * States that the {@link Field} with this type represents a field who's type is an entity.
         * This states, that there are {@link Field}s existing for the fields of that class / object.
         */
        ENTITY,

        /**
         * WIP: To indicate boolean fields
         */
        CONDITIONAL
    }
}
