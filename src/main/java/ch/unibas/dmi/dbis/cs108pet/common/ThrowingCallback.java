package ch.unibas.dmi.dbis.cs108pet.common;

/**
 * @author loris.sauter
 */
@FunctionalInterface
@Deprecated
public interface ThrowingCallback extends ThrowingFunction<Void, Void> {
  
  void callThrowing() throws Exception;
  
  @Override
  default Void applyThrowing(Void aVoid) {
    try {
      callThrowing();
      return null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
