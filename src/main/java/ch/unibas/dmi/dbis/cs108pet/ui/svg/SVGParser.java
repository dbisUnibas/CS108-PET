package ch.unibas.dmi.dbis.cs108pet.ui.svg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class SVGParser {
  
  private static final String SUFFIX = ".svg";
  public static final String SVG_TAG_NAME = "svg";
  public static final String PAINT_NONE = "none";
  public static final String PAINT_CURRENT_COLOR = "currentColor";
  public static final String LINE_X1_KEY = "x1";
  public static final String LINE_Y1_KEY = "y1";
  public static final String LINE_X2_KEY = "x2";
  public static final String LINE_Y2_KEY = "y2";
  private XmlMapper mapper = new XmlMapper();
  
  public static final String WIDTH_KEY = "width";
  public static final String HEIGHT_KEY = "height";
  public static final String VIEW_BOX_KEY = "viewBox";
  public static final String FILL_KEY = "fill";
  public static final String STROKE_KEY = "stroke";
  public static final String STROKE_WIDTH_KEY = "stroke-width";
  public static final String STROKE_LINECAP_KEY = "stroke-linecap";
  public static final String STROKE_LINEJOIN_KEY = "stroke-linejoin";
  public static final String CLASS_KEY = "class";
  public static final String LINE_KEY = "line";
  public static final String POLYLINE_KEY = "polyline";
  public static final String POINTS_KEY = "points";
  public static final String PATH_KEY = "path";
  public static final String POLYGON_KEY = "polygon";
  public static final String CIRCLE_KEY = "circle";
  public static final String ELLIPSE_KEY = "ellipse";
  public static final String MESH_KEY = "mesh";
  public static final String RECT_KEY = "rect";
  
  public static final String[] SUPPORTED_SHAPES = new String[]{LINE_KEY, POLYLINE_KEY};
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  @Deprecated
  public JsonNode load(String path) throws IOException {
    path = normalize(path);
    LOGGER.trace("Loading from {}", path);
    URL url = getClass().getClassLoader().getResource(path);
    LOGGER.trace("Found: {}", url);
    
    if (url == null) {
      throw new FileNotFoundException("Could not find svg file: " + path);
    }
    
    JsonNode root = mapper.readTree(url);
    LOGGER.debug("Read: {}", root);
    
    
    return root;
  }
  
  public SVGDescription parse(String path) throws ParserConfigurationException, IOException, SAXException {
    path = normalize(path);
    InputStream is = getClass().getClassLoader().getResourceAsStream(path);
    LOGGER.trace("Found: {}", is != null);
    if (is == null) {
      throw new FileNotFoundException("Could not find svg file: " + path);
    }
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document doc = db.parse(is);
    LOGGER.trace("Root: {}", doc.getDocumentElement());
    if (!doc.getDocumentElement().getNodeName().equals(SVG_TAG_NAME)) {
      throw new IllegalArgumentException("Cannot parse non-svg xml file");
    }
    NodeList childs = doc.getDocumentElement().getChildNodes();
    LOGGER.debug("Tags: {}", getTagNames(childs));
    List<String> tags = getTagNames(childs);
    List<String> occurringSupportedShapes = tags.stream().filter(s -> Arrays.asList(SUPPORTED_SHAPES).contains(s)).collect(Collectors.toList());
    LOGGER.debug("Occurring and supported shapes: {}", occurringSupportedShapes);
    if (occurringSupportedShapes.isEmpty()) {
      throw new UnsupportedOperationException("One or multiple shapes are unsupported (Shapes: " + tags.toString() + ")");
    }
    SVGDescription desc = parseSVG(doc.getDocumentElement());
    return desc;
  }
  
  private SVGDescription parseSVG(Element svg) {
    SVGDescription desc = new SVGDescription();
    
    desc.setHeight(Double.valueOf(svg.getAttribute(HEIGHT_KEY)));
    desc.setWidth(Double.valueOf(svg.getAttribute(WIDTH_KEY)));
    desc.setClassNames(Arrays.asList(svg.getAttribute(CLASS_KEY).split(" ")));
    desc.setFill(parsePaint(svg.getAttribute(FILL_KEY)));
    desc.setStroke(parsePaint(svg.getAttribute(STROKE_KEY)));
    desc.setStrokeWidth(Double.valueOf(svg.getAttribute(STROKE_WIDTH_KEY)));
    desc.setStrokeLinecap(StrokeLineCap.valueOf(svg.getAttribute(STROKE_LINECAP_KEY).toUpperCase()));
    desc.setStrokeLinejoin(StrokeLineJoin.valueOf(svg.getAttribute(STROKE_LINEJOIN_KEY).toUpperCase()));
    desc.setViewBox(parseViewbox(svg.getAttribute(VIEW_BOX_KEY)));
    NodeList children = svg.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      switch (node.getNodeName()) {
        case LINE_KEY:
          desc.addShape(parseLine(node));
          break;
        case POLYLINE_KEY:
          desc.addShape(parsePolyline(node));
          break;
        default:
          throw new IllegalArgumentException("Cannot parse this: " + node.getNodeName());
      }
    }
    return desc;
  }
  
  private SVGShape parsePolyline(Node node) {
    LOGGER.debug("Polyline: {}", node.getAttributes().getNamedItem(POINTS_KEY));
    String line = node.getAttributes().getNamedItem(POINTS_KEY).getNodeValue();
    String[] values = line.split(" ");
    double[] coords = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      coords[i] = Double.valueOf(values[i]);
    }
    return new SVGPolyline(coords);
  }
  
  private SVGLine parseLine(Node node) {
    LOGGER.debug("Line: ");
    double x1 = Double.valueOf(node.getAttributes().getNamedItem(LINE_X1_KEY).getNodeValue());
    double y1 = Double.valueOf(node.getAttributes().getNamedItem(LINE_Y1_KEY).getNodeValue());
    double x2 = Double.valueOf(node.getAttributes().getNamedItem(LINE_X2_KEY).getNodeValue());
    double y2 = Double.valueOf(node.getAttributes().getNamedItem(LINE_Y2_KEY).getNodeValue());
    
    return new SVGLine(x1, y1, x2, y2);
  }
  
  private double[] parseViewbox(String viewBox) {
    double[] out = new double[4];
    if (viewBox.contains(",")) {
      viewBox = viewBox.trim();
      viewBox = viewBox.replace(",", " ");
    }
    String[] values = viewBox.split(" ");
    if (values.length != 4) {
      throw new IllegalArgumentException("Illegal amount of viewbox numbers");
    }
    for (int i = 0; i < 4; i++) {
      out[i] = Double.valueOf(values[i]);
    }
    return out;
  }
  
  private Paint parsePaint(String paint) {
    if (paint.equals(PAINT_NONE)) {
      return SVGUtils.NONE;
    } else if (paint.equals(PAINT_CURRENT_COLOR)) {
      return null;
    } else {
      return Color.web(paint); // TODO may change in future
    }
  }
  
  
  private List<String> getTagNames(NodeList list) {
    List<String> names = new ArrayList<>();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i) != null) {
        names.add(list.item(i).getNodeName());
      }
    }
    return names;
  }
  
  private static String normalize(String str) {
    if (!str.endsWith(SUFFIX)) {
      return str + SUFFIX;
    } else {
      return str;
    }
  }
}
