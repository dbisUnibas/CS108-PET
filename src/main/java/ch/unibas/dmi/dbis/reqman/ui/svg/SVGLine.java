package ch.unibas.dmi.dbis.reqman.ui.svg;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class SVGLine implements SVGShape {
  
  public static final String LINE_KEY = "line";
  private double x1, y1, x2, y2;
  
  public SVGLine(double x1, double y1, double x2, double y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }
  
  @Override
  public String getName() {
    return LINE_KEY;
  }
  
  
  public double getX1() {
    return x1;
  }
  
  public double getX2() {
    return x2;
  }
  
  public double getY1() {
    return y1;
  }
  
  public double getY2() {
    return y2;
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("SVGLine{");
    sb.append("name='").append(getName()).append('\'');
    sb.append(", x1=").append(x1);
    sb.append(", y1=").append(y1);
    sb.append(", x2=").append(x2);
    sb.append(", y2=").append(y2);
    sb.append('}');
    return sb.toString();
  }
}
