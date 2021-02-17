package ch.unibas.dmi.dbis.cs108pet.ui.svg;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class SVGPolyline implements SVGShape {
  
  
  public static final String POLYLINE_KEY = "polyline";
  
  private int size;
  private double[] xPoints;
  private double[] yPoints;
  
  @Override
  public String getName() {
    return POLYLINE_KEY;
  }
  
  public SVGPolyline() {
    size = 0;
  }
  
  public SVGPolyline(double... coordinates) {
    size = coordinates.length / 2; // Intentionally integer division: svg specs say, if odd number, omit last - invalid - coordinate.
    yPoints = new double[size];
    xPoints = new double[size];
    for (int i = 0; i < size; i++) {
      xPoints[i] = coordinates[i * 2];
      yPoints[i] = coordinates[i * 2 + 1];
    }
  }
  
  public SVGPolyline(double[] xPoints, double[] yPoints) {
    this.xPoints = xPoints;
    this.yPoints = yPoints;
    size = xPoints.length;
  }
  
  public int size() {
    return size;
  }
  
  public double[] getXPoints() {
    return xPoints;
  }
  
  public double[] getYPoints() {
    return yPoints;
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("SVGPolyline{");
    sb.append("name='").append(getName()).append('\'');
    sb.append(", size=").append(size);
    sb.append(", XPoints=");
    if (xPoints == null) sb.append("null");
    else {
      sb.append('[');
      for (int i = 0; i < xPoints.length; ++i)
        sb.append(i == 0 ? "" : ", ").append(xPoints[i]);
      sb.append(']');
    }
    sb.append(", YPoints=");
    if (yPoints == null) sb.append("null");
    else {
      sb.append('[');
      for (int i = 0; i < yPoints.length; ++i)
        sb.append(i == 0 ? "" : ", ").append(yPoints[i]);
      sb.append(']');
    }
    sb.append('}');
    return sb.toString();
  }
}
