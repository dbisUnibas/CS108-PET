package ch.unibas.dmi.dbis.reqman.storage;

import java.util.UUID;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class UuidMismatchException extends Exception {
  
  private final UUID expected;
  private final UUID actual;
  
  public UuidMismatchException(UUID expected, UUID actual) {
    super(String.format("Expected %s, found: %s", expected, actual));
    this.expected = expected;
    this.actual = actual;
  }
  
  public UuidMismatchException(String message, UUID expected, UUID actual) {
    super(message);
    this.expected = expected;
    this.actual = actual;
  }
  
  public UuidMismatchException(String message, Throwable cause, UUID expected, UUID actual) {
    super(message, cause);
    this.expected = expected;
    this.actual = actual;
  }
  
  public UuidMismatchException(Throwable cause, UUID expected, UUID actual) {
    super(cause);
    this.expected = expected;
    this.actual = actual;
  }
  
  public UUID getExpected() {
    return expected;
  }
  
  public UUID getActual() {
    return actual;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    
    UuidMismatchException that = (UuidMismatchException) o;
    
    if (!getExpected().equals(that.getExpected())) return false;
    return getActual().equals(that.getActual());
  }
  
  @Override
  public int hashCode() {
    int result = getExpected().hashCode();
    result = 31 * result + getActual().hashCode();
    return result;
  }

}
