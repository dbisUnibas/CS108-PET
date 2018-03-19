package ch.unibas.dmi.dbis.reqman.ui.svg;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class SVGLoader {
  
  private static SVGLoader instance = null;
  
  public static SVGLoader getInstance(){
    if(instance == null){
      instance = new SVGLoader();
    }
    return instance;
  }
  
  private Map<String, SVGDescription> cache;
  private SVGParser parser = new SVGParser();
  
  private SVGLoader(){
    cache = new HashMap<>();
  }
  
  public SVGNode load(String path) throws IOException {
    try {
      SVGDescription desc;
      if(cache.containsKey(path)){
        desc =  cache.get(path);
      }else{
        desc = parser.parse(path);
      }
      return new SVGNode(desc);
    } catch (ParserConfigurationException | SAXException e) {
      // TODO Add smart handling of exceptions
      throw new RuntimeException("Couldn't load svg node. ", e);
    }
  }
}
