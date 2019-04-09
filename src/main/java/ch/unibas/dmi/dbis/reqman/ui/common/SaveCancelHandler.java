package ch.unibas.dmi.dbis.reqman.ui.common;

import javafx.event.ActionEvent;

/**
 * Event handling interface for save & cancel buttons.
 *
 * @author loris.sauter
 */
public interface SaveCancelHandler {

  /**
   * Saves whatever needs to get saved from the perspective of the event.
   * The event <b>must not</b> be consumed!
   *
   * @param event The event that was fired by the save-button.
   */
  void save(ActionEvent event);

  /**
   * Cancels whatever needs to get canceled from the perspective of the event.
   * The event <b>must not</b> be consumed!
   *
   * @param event The event that was fired by the cancel-button.
   */
  void cancel(ActionEvent event);
}
