package ch.unibas.dmi.dbis.reqman.ui.svg;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class SVGNode extends Region {
  
  
  private Canvas canvas;
  private SVGDescription desc;
  
  SVGNode(SVGDescription description) {
    canvas = new Canvas(description.getWidth(), description.getHeight());
    this.desc = description;
    init();
    setup();
  }
  
  private void init() {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    
    gc.setFill(desc.getFill());
    gc.setStroke(desc.getStroke());
    gc.setLineCap(desc.getStrokeLinecap());
    gc.setLineJoin(desc.getStrokeLinejoin());
    gc.setLineWidth(desc.getStrokeWidth());
    super.getStyleClass().addAll(desc.getClassNames());
    for (SVGShape shape : desc.getShapes()) {
      if (shape instanceof SVGLine) {
        SVGLine line = (SVGLine) shape;
        gc.strokeLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
      } else if (shape instanceof SVGPolyline) {
        SVGPolyline polyline = (SVGPolyline) shape;
        gc.strokePolyline(polyline.getXPoints(), polyline.getYPoints(), polyline.size());
      }
      // TODO more to come
    }
  }
  
  private void setup() {
    super.getChildren().add(canvas);
  }
  
  public SVGNode copy() {
    return new SVGNode(desc);
  }
}
