package ch.unibas.dmi.dbis.reqman.ui.svg;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class SVGLoader {
  
  private SVGLoader(){
    // No instance
  }
  
  // TODO make singleton and add cache, so that only once parsed.
  
  public static SVGNode load(String path) throws IOException {
    SVGParser parser = new SVGParser();
    try {
      SVGDescription desc = parser.parse(path);
      return new SVGNode(desc);
    } catch (ParserConfigurationException | SAXException e) {
      // TODO Add smart handling of exceptions
      throw new RuntimeException("Couldn't load svg node. ", e);
    }
  }
}
