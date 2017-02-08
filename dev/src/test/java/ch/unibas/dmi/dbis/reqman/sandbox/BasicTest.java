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
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class BasicTest {

    private ProgressSummary ps = new ProgressSummary("MS", "internal", "external");
    private File file = new File("test.json");

    @Before
    public void setUp() throws Exception {
        IOUtilites.writeToJSONFile(ps, file);
    }

    @Test
    public void testReadAndEquals() throws IOException {
        ProgressSummary test = (ProgressSummary) IOUtilites.readFromJSONFile(file, ProgressSummary.class);
        assertEquals(ps, test);
    }

    @After
    public void tearDown() throws Exception {
        file.deleteOnExit();
    }

}