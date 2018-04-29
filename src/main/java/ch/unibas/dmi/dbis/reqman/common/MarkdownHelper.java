package ch.unibas.dmi.dbis.reqman.common;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class MarkdownHelper {
  
  private MarkdownHelper(){
    // No instance needed
  }
  
  public static String loadAndRender(String path) throws IOException {
    InputStream instream = MarkdownHelper.class.getClassLoader().getResourceAsStream(path);
  
    BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
    
    Parser parser = Parser.builder().build();
    Node document = parser.parseReader(reader);
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    return renderer.render(document);
  }
}
