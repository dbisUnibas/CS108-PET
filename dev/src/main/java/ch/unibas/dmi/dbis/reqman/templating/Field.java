package ch.unibas.dmi.dbis.reqman.templating;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.Function;

/**
 * The class {@link Field} represents a field (property, attribute) of the related entity.
 * This class is used to define renderer / to oldRender fields in a later stage.
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
 *     Field<Person, String> nameField = new Filed<>("name", Field.Type.NORMAL, Field::getName);
 *     Field<Person, Integer> ageField = new Field<>("age", Field.Type.NORMAL, Field::getAge);
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
    protected String name;
    /**
     * The field's type
     */
    protected Type type;
    /**
     * The getter for the field
     */
    protected Function<E, T> getter;
    /**
     * The renderer for the field, if the field's type is {@link Type#OBJECT}
     */
    protected Function<T, String> renderer = null;


    /**
     * Constructor for a new {@link Field} with specified properties.
     * <p>
     *
     * @param name   The name of the field
     * @param type   The field's type.
     * @param getter The getter used to get the field's value.
     * @throws IllegalArgumentException If the type is {@link Type#OBJECT}
     */
    protected Field(String name, Type type, Function<E, T> getter) throws IllegalArgumentException {
        if (type == Type.OBJECT || type == Type.LIST) {
            throw new IllegalArgumentException("If this field's type is Type.OBJECT or Type.LIST, an appropriate *renderer must be provided*. See the docs for more information");
        }
        this.name = name;
        this.type = type;
        this.getter = getter;

    }

    /**
     * Constructs a new {@link Field} with specified properties and with a renderer provided.
     * <p>
     * This constructor is intended to use if the field's type is of type {@link Type#OBJECT}.
     * The field then must know how to oldRender itself and thus needs to have a {@code renderer} specified.
     * <p>
     * A a {@code renderer} is any arbitrary {@link Function} which returns a {@link String} when given an object
     * of this field's type. Refer to the classes introduction to understand why and how a renderer is provided.
     *
     * @param name     The name of the field
     * @param type     The field's type
     * @param getter   The getter used to get the field's value
     * @param renderer The renderer which renders this field.
     */
    protected Field(String name, Type type, Function<E, T> getter, Function<T, String> renderer) {
        this.name = name;
        this.type = type;
        this.getter = getter;
        this.renderer = renderer;
    }

    /**
     * Creates a new {@link Field} with specified properties.
     * <p>
     * This creates a normal {@link Field}.
     *
     * @param name   The name of the field
     * @param getter The getter used to get the field's value.
     * @param <E> The type of the entity this {@link Field} is for
     * @param <T> The type of the field this {@link Field} represents
     * @return A new {@link Field}.
     */
    public static <E,T> Field<E, T> createNormalField(String name, Function<E,T> getter){
        return new Field<>(name, Type.NORMAL, getter);
    }

    /**
     * Creates a new {@link Field} with specified properties and with a renderer provided.
     * <p>
     * The {@link Field} then must know how to oldRender itself and thus needs to have a {@code renderer} specified.
     * <p>
     * A a {@code renderer} is any arbitrary {@link Function} which returns a {@link String} when given an object
     * of this {@link Field}'s type. Refer to the classes introduction to understand why and how a renderer is provided.
     *
     * @param name     The name of the field
     * @param getter   The getter used to get the field's value
     * @param renderer The renderer which renders this field.
     * @param <E> The type of the enitity this {@link Field} is for
     * @param <T> The type of the field this {@link Field} represents
     * @return
     */
    public static <E,T> Field<E,T> createObjectField(String name, Function<E,T> getter, Function<T, String> renderer){
        return new Field<>(name, Type.OBJECT, getter, renderer);
    }

    public static <E,T> Field<E,T> createListField(String name, Function<E,T> getter, Function<T, String> renderer){
        return new Field<>(name, Type.LIST, getter, renderer);
    }

    // TODO Write factory methods for all types!

    /**
     * Renders the instance's field.
     * Be aware that this method returns null if the type is {@link Type#SUB_ENTITY}
     *
     * @param instance The instance from where the getter gets its resulting value.
     * @return Either the rendered field or null, if the type is {@link Type#SUB_ENTITY}
     */
    public String render(E instance) {
        T value = getter.apply(instance);
        switch (type) {
            case NORMAL:
                if(getRenderer() == null){
                    if(value instanceof Double){
                        /*
                        Algorithm by: http://stackoverflow.com/a/25308216
                         */
                        Double d = (Double)value;
                        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                        df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
                        return df.format(d);
                    }
                    return String.valueOf(value);
                }else{
                    return renderer.apply(value);
                }
            case OBJECT:
            case LIST:
            case PARAMETRIZED:
                return renderer.apply(value);
            case SUB_ENTITY:
                return null;
            default:
                throw new IllegalArgumentException("Cannot render a field of type: "+type.toString());
        }
    }

    protected Function<T, String> getRenderer(){
        return renderer;
    }

    protected void setRenderer(Function<T, String> renderer){
        this.renderer = renderer;
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

    public static <E,T> Field<E,T> copy(Field<E,T> source){
        Field<E,T> copy = new Field<E, T>(source.getName(), source.getType(), source.getGetter(), source.getRenderer() );
        return copy;
    }

    /**
     * The enumeration {@link Type} is used to differentiate between different types of {@link Field}s.
     */
    public enum Type {
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
        NORMAL,
        /**
         * States that the {@link Field} with this type represents a field who's type is any arbitrary class.
         */
        OBJECT,
        /**
         * States that the {@link Field} with this type represents a field who's type is an entity.
         * This states, that there are {@link Field}s existing for the fields of that class / object.
         */
        SUB_ENTITY,

        /**
         * To indicate boolean fields
         */
        CONDITIONAL,

        /**
         * To indicate fields that have options
         */
        PARAMETRIZED,
        /**
         * To indicate that the field represented by the {@link Field} with this type is a list and therefore cannot be rendered.
         */
        LIST
    }
}
