package ch.unibas.dmi.dbis.reqman.configuration;

/**
 * A {@link RuntimeException} occurred during configuration reading.
 * <p>
 * This simply is a mask of an ordinary {@link RuntimeException}.
 *
 * @author loris.sauter
 */
public class ConfigurationException extends RuntimeException {

  /**
   * The default constructor for {@link ConfigurationException} with a default message
   */
  public ConfigurationException() {
    super("An exception occurred while reading the configuration");
  }

  /**
   * Constructs a new {@link ConfigurationException} with specified exception message
   *
   * @param message The detailed exception message
   */
  public ConfigurationException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@link ConfigurationException} with a detailed message and a specified cause
   *
   * @param message The detailed message
   * @param cause   The cause of this exception
   */
  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new {@link ConfigurationException} with a specified cause
   *
   * @param cause The cause of this exception
   */
  public ConfigurationException(Throwable cause) {
    super(cause);
  }

}
