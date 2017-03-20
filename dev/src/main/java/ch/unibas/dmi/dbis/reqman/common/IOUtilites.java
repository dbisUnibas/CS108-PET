package ch.unibas.dmi.dbis.reqman.common;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class IOUtilites {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void writeToJSONFile(Object obj, File file) throws IOException {
        MAPPER.writeValue(file, obj);
    }

    public static Object readFromJSONFile(File file, Class<?> clazz) throws IOException {
        return MAPPER.readValue(file, clazz);
    }

    public static Catalogue readCatalogueJSONFile(File file) throws IOException {
        return (Catalogue) readFromJSONFile(file, Catalogue.class);
    }


    public static Group readFromJSONFile(File file) throws IOException {
        return (Group) readFromJSONFile(file, Group.class);
    }
}
