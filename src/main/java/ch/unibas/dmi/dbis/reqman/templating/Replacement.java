package ch.unibas.dmi.dbis.reqman.templating;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class Replacement<E> implements Renderer<E> {
  
  private Field<E, ?> field;
  @Deprecated
  private int start;
  @Deprecated
  private int end;
  @Deprecated
  private String targetRegex;
  private String targetExpression;
  
  public Replacement(Field<E, ?> field, int start, int end, String targetRegex, String targetExpression) {
    this.field = field;
    this.start = start;
    this.end = end;
    this.targetRegex = targetRegex;
    this.targetExpression = targetExpression;
  }
  
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Replacement{");
    sb.append("field=");
    if (field instanceof Field) {
      sb.append(field != null ? field.toString() : "null");
    } else if (field instanceof SubEntityField) {
      SubEntityField subField = (SubEntityField) field;
      sb.append(subField.toString());
    } else {
      sb.append(field != null ? field.toString() : "null");
    }
    sb.append(", start=").append(start);
    sb.append(", end=").append(end);
    sb.append(", targetRegex='").append(targetRegex).append('\'');
    sb.append(", targetExpression='").append(targetExpression).append('\'');
    sb.append('}');
    return sb.toString();
  }
  
  public Field<E, ?> getField() {
    return field;
  }
  
  public void setField(Field<E, ?> field) {
    this.field = field;
  }
  
  @Deprecated
  public int getStart() {
    return start;
  }
  
  public void setStart(int start) {
    this.start = start;
  }
  
  @Deprecated
  public int getEnd() {
    return end;
  }
  
  public void setEnd(int end) {
    this.end = end;
  }
  
  public String getTargetRegex() {
    return targetRegex;
  }
  
  public void setTargetRegex(String targetRegex) {
    this.targetRegex = targetRegex;
  }
  
  @Override
  public String render(E instance) {
    return field.render(instance);
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    
    Replacement<?> that = (Replacement<?>) o;
    
    if (start != that.start) {
      return false;
    }
    if (end != that.end) {
      return false;
    }
    if (field != null ? !field.equals(that.field) : that.field != null) {
      return false;
    }
    if (targetRegex != null ? !targetRegex.equals(that.targetRegex) : that.targetRegex != null) {
      return false;
    }
    return targetExpression != null ? targetExpression.equals(that.targetExpression) : that.targetExpression == null;
  }
  
  @Override
  public int hashCode() {
    int result = field != null ? field.hashCode() : 0;
    result = 31 * result + start;
    result = 31 * result + end;
    result = 31 * result + (targetRegex != null ? targetRegex.hashCode() : 0);
    result = 31 * result + (targetExpression != null ? targetExpression.hashCode() : 0);
    return result;
  }
  
  public String getTargetExpression() {
    return targetExpression;
  }
  
  public void setTargetExpression(String targetExpression) {
    this.targetExpression = targetExpression;
  }
}
