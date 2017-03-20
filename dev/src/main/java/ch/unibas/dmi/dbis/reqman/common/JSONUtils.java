package ch.unibas.dmi.dbis.reqman.common;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class JSONUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String toJSON(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    public static void writeToJSONFile(Object obj, File file) throws IOException {
        MAPPER.writeValue(file, obj);
    }

    public static <T> T readFromJSONFile(File file, Class<T> clazz) throws IOException {
        return MAPPER.readValue(file, clazz);
    }

    public static <T> T readFromString(String str, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        return MAPPER.readValue(str, clazz);
    }

    public static Catalogue readCatalogueJSONFile(File file) throws IOException {
        return readFromJSONFile(file, Catalogue.class);
    }


    public static Group readGroupJSONFile(File file) throws IOException {
        return readFromJSONFile(file, Group.class);
    }
}
