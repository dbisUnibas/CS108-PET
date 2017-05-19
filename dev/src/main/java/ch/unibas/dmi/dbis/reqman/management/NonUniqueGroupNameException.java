package ch.unibas.dmi.dbis.reqman.management;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class NonUniqueGroupNameException extends RuntimeException {


    private final String name;

    public NonUniqueGroupNameException(String name) {
        super("Group name not unique");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
