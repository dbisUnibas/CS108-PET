package ch.unibas.dmi.dbis.reqman.ui.svg;

import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class SVGDescription {
  
  /**
   * see https://www.w3.org/TR/SVG2/geometry.html#Sizing
   */
  private double width;
  /**
   * see https://www.w3.org/TR/SVG2/geometry.html#Sizing
   */
  private double height;
  /**
   * see https://www.w3.org/TR/SVG2/coords.html#ViewBoxAttribute
   */
  private double[] viewBox;
  
  /**
   * see https://www.w3.org/TR/SVG2/painting.html#FillProperty
   */
  private Paint fill;
  
  /**
   * see https://www.w3.org/TR/SVG2/painting.html#StrokeProperty
   */
  private Paint stroke;
  
  /**
   * see https://www.w3.org/TR/SVG2/painting.html#StrokeWidthProperty
   */
  private double strokeWidth;
  /**
   * see https://www.w3.org/TR/SVG2/painting.html#StrokeLinecapProperty
   */
  private StrokeLineCap strokeLinecap;
  /**
   * see https://www.w3.org/TR/SVG2/painting.html#StrokeLinejoinProperty
   */
  private StrokeLineJoin strokeLinejoin;
  /**
   * see https://www.w3.org/TR/SVG2/styling.html#ClassAttribute
   */
  private List<String> classNames;
  
  /**
   * see https://www.w3.org/TR/SVG2/shapes.html#TermShapeElement
   */
  private List<SVGShape> shapes;
  
  SVGDescription() {
    shapes = new ArrayList<>();
    classNames = new ArrayList<>();
  }
  
  public List<SVGShape> getShapes() {
    return shapes;
  }
  
  public void addShape(SVGShape shape) {
    if (shapes == null) {
      shapes = new ArrayList<>();
      shapes.add(shape);
    } else {
      shapes.add(shape);
    }
  }
  
  void setShapes(List<SVGShape> shapes) {
    this.shapes = shapes;
  }
  
  public double getWidth() {
    return width;
  }
  
  public double getHeight() {
    return height;
  }
  
  public double[] getViewBox() {
    return viewBox;
  }
  
  public Paint getFill() {
    return fill;
  }
  
  public Paint getStroke() {
    return stroke;
  }
  
  public double getStrokeWidth() {
    return strokeWidth;
  }
  
  public StrokeLineCap getStrokeLinecap() {
    return strokeLinecap;
  }
  
  public StrokeLineJoin getStrokeLinejoin() {
    return strokeLinejoin;
  }
  
  
  void setWidth(double width) {
    this.width = width;
  }
  
  void setHeight(double height) {
    this.height = height;
  }
  
  void setViewBox(double[] viewBox) {
    this.viewBox = viewBox;
  }
  
  void setFill(Paint fill) {
    this.fill = fill;
  }
  
  void setStroke(Paint stroke) {
    this.stroke = stroke;
  }
  
  void setStrokeWidth(double strokeWidth) {
    this.strokeWidth = strokeWidth;
  }
  
  void setStrokeLinecap(StrokeLineCap strokeLinecap) {
    this.strokeLinecap = strokeLinecap;
  }
  
  void setStrokeLinejoin(StrokeLineJoin strokeLinejoin) {
    this.strokeLinejoin = strokeLinejoin;
  }
  
  public List<String> getClassNames() {
    return classNames;
  }
  
  void setClassNames(List<String> classNames) {
    this.classNames = classNames;
  }
  
  public boolean hasStrokeCurrentColor() {
    return stroke == null;
  }
  
  public boolean hasFillCurrentColor() {
    return fill == null;
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("SVGDescription{");
    sb.append("width=").append(width);
    sb.append(", height=").append(height);
    sb.append(", viewBox=");
    if (viewBox == null) sb.append("null");
    else {
      sb.append('[');
      for (int i = 0; i < viewBox.length; ++i)
        sb.append(i == 0 ? "" : ", ").append(viewBox[i]);
      sb.append(']');
    }
    sb.append(", fill='").append(fill).append('\'');
    sb.append(", stroke=").append(stroke);
    sb.append(", strokeWidth=").append(strokeWidth);
    sb.append(", strokeLinecap='").append(strokeLinecap).append('\'');
    sb.append(", strokeLinejoin='").append(strokeLinejoin).append('\'');
    sb.append(", classNames=").append(classNames);
    sb.append(", shapes=").append(shapes);
    sb.append('}');
    return sb.toString();
  }
}
