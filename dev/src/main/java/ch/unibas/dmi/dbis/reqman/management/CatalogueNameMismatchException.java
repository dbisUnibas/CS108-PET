package ch.unibas.dmi.dbis.reqman.management;

import java.io.File;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class CatalogueNameMismatchException extends RuntimeException {

    private final String catName;
    private final String groupCatName;
    private final String groupName;

    private final File groupFile;

    public CatalogueNameMismatchException(String catName, String groupCatName, String groupName, File groupFile) {
        super("Catalogue names mismatch.");
        this.catName = catName;
        this.groupCatName = groupCatName;
        this.groupName = groupName;
        this.groupFile = groupFile;
    }

    public String getCatName() {
        return catName;
    }

    public String getGroupCatName() {
        return groupCatName;
    }

    public String getGroupName() {
        return groupName;
    }

    public File getGroupFile() {
        return groupFile;
    }
}
