package ch.unibas.dmi.dbis.reqman.common;

import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Group;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * The applications sinlge entry point / interface with faster-xml/jackson.
 *
 *
 * @author loris.sauter
 */
public class JSONUtils {

    /**
     * The single ObjectMapper instance
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Enables pretty print per default
     */
    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Converts the given object into its JSON representation.
     * @param obj The object to convert
     * @return The object represented as a string in JSON notation
     * @throws JsonProcessingException
     * @see ObjectMapper#writeValueAsString(Object)
     */
    public static String toJSON(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    /**
     * Writes the given object into the specified file as a JSON file.
     * @param obj The object which sould be written as JSON into the given file
     * @param file The target file to write the JSON string into
     * @throws IOException
     * @see ObjectMapper#writeValue(File, Object)
     */
    public static void writeToJSONFile(Object obj, File file) throws IOException {
        MAPPER.writeValue(file, obj);
    }

    /**
     * Reads from the given file an object of spiefied class.
     * @param file The file to read from
     * @param clazz The class of the object which is JSON encoded in the file
     * @param <T> The type of the object to return
     * @return An object of type T, with its value read from the JSON representation in file
     * @throws IOException
     * @see ObjectMapper#readValue(File, Class)
     */
    public static <T> T readFromJSONFile(File file, Class<T> clazz) throws IOException {
        return MAPPER.readValue(file, clazz);
    }

    /**
     * Reads from the given string an object of specified class
     * @param str The string to read from
     * @param clazz The class of the object which is JSON encoded
     * @param <T> The type of the object to return
     * @return An object of type T, with its value read from the JSON representation in string
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     * @see ObjectMapper#readValue(String, Class)
     */
    public static <T> T readFromString(String str, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        return MAPPER.readValue(str, clazz);
    }

    /**
     * Reads a catalogue from a given file.
     * The file must be a file in JSON format, containing the fields of a catalogue
     * @param file The catalogue file to read from
     * @return The catalogue which was encoded in the file
     * @throws IOException
     */
    public static Catalogue readCatalogueJSONFile(File file) throws IOException {
        return readFromJSONFile(file, Catalogue.class);
    }


    /**
     * Reads a group from a given file.
     * The file must be a file in JSON format, containin the fields of a group
     * @param file The group file to read form
     * @return The group which was encoded in the file
     * @throws IOException
     */
    public static Group readGroupJSONFile(File file) throws IOException {
        return readFromJSONFile(file, Group.class);
    }

    /**
     * Reads a given JSON file and reaturns a map of key-value pairs.
     * @param file The file to read from
     * @return A map with key (string) and value (object) pairs, representing the JSON structure of the file
     * @throws IOException
     */
    public static Map<String, Object> readFromJSONFile(File file) throws IOException {
        return MAPPER.readValue(file, new TypeReference<Map<String, Object>>() {
        });
    }
}
