package ch.unibas.dmi.dbis.cs108pet.ui.common;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class MandatoryFieldsMissingException extends RuntimeException {
  
  public static final String DEFAULT_MESSAGE = "One or more mandatory fields are not set";
  private String formattedMessage = "";
  
  private MandatoryFieldsMissingException(String msg) {
    super(msg);
  }
  
  public MandatoryFieldsMissingException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public MandatoryFieldsMissingException(Throwable cause) {
    super(DEFAULT_MESSAGE, cause);
  }
  
  public MandatoryFieldsMissingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
  
  public String getFormattedMessage() {
    return formattedMessage;
  }
  
  public static MandatoryFieldsMissingException createWithFormattedMessage(String message) {
    MandatoryFieldsMissingException ex = new MandatoryFieldsMissingException(DEFAULT_MESSAGE);
    ex.formattedMessage = message;
    return ex;
  }
  
  public static MandatoryFieldsMissingException createWithFormattedMessage(String message, Throwable cause) {
    MandatoryFieldsMissingException ex = new MandatoryFieldsMissingException(cause);
    ex.formattedMessage = message;
    return ex;
  }
}
