package ch.unibas.dmi.dbis.reqman.sandbox;

import ch.unibas.dmi.dbis.reqman.common.IOUtilites;
import ch.unibas.dmi.dbis.reqman.core.ProgressSummary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * BasicTest is a simple junit test case to check rudimentary functionalities of reqman and jackson-databind.
 *
 *
 * @author loris.sauter
 */
public class BasicTest {

    /**
     * The expected PS to be template for the file and assertion.
     */
    private ProgressSummary ps = new ProgressSummary("MS", "internal", "external");
    private File file = new File("test.json");

    /**
     * Creates the json file to read from
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        IOUtilites.writeToJSONFile(ps, file);
    }

    /**
     * Tests whether the {@link IOUtilites#readFromJSONFile(File, Class)} works.
     * As a consequence it tests if {@link ProgressSummary#equals(Object)} works properly.
     * @throws IOException If the reading of the json file fails
     */
    @Test
    public void testReadAndEquals() throws IOException {
        ProgressSummary test = (ProgressSummary) IOUtilites.readFromJSONFile(file, ProgressSummary.class);
        assertEquals(ps, test);
    }

    /**
     * Removes the json file
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        file.deleteOnExit();
    }

}