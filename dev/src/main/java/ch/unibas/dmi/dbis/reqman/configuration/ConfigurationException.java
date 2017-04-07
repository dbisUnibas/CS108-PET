package ch.unibas.dmi.dbis.reqman.configuration;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException() {
        super("An exception occurred while reading the configuration");
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    public ConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
