package ch.unibas.dmi.dbis.reqman.ui.common;

import java.util.function.Consumer;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class PromptPopup<T> {
  
  protected AbstractVisualCreator<T> creator;
  protected PopupStage stage;
  
  
  private Consumer<T> consumer = null;
  
  public PromptPopup(AbstractVisualCreator creator) {
    this(creator, true);
  }
  
  public PromptPopup(AbstractVisualCreator creator, Consumer consumer) {
    this(creator, false);
    this.consumer = consumer;
    
    
  }
  
  private PromptPopup(AbstractVisualCreator creator, boolean modality) {
    this.creator = creator;
    stage = new PopupStage(creator.getPromptTitle(), creator, modality);
    stage.setOnHiding(evt -> {
      if (consumer != null) {
        if (getCreation() != null) {
          consumer.accept(getCreation());
        }
      }
    });
  }
  
  /**
   * Returns a new instance which was created by the Creator.
   * If the user did cancel the creation process null will be returned, so check for this event.
   *
   * @return A newly created instance of T or null, if the user did cancel the creation.
   */
  public T prompt() {
    try {
      stage.showAndWait();
    } catch (MandatoryFieldsMissingException e) {
      Utils.showErrorDialog("Fields Missing", "Fields Missing", e.getFormattedMessage());
    }
    
    return getCreation();
  }
  
  public void showPrompt() {
    stage.show();
  }
  
  /**
   * Returns a new instance of which was created by the Creator.
   *
   * @return the created instance OR null, if the user canceled the prompt.
   */
  public T getCreation() {
    if (creator.isCreatorReady()) {
      return creator.create();
    } else {
      return null;
    }
  }
  
}
