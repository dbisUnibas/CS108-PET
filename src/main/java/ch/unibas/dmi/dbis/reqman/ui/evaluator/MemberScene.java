package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.data.Member;
import ch.unibas.dmi.dbis.reqman.ui.common.AbstractVisualCreator;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.lang.StringUtils;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class MemberScene extends AbstractVisualCreator<Member> {
  
  private Member member = null;
  
  private TextField tfName, tfFirstName, tfEmail;
  
  public MemberScene() {
    super();
    populateScene();
  }
  
  @Override
  public String getPromptTitle() {
    return "Add new Member";
  }
  
  @Override
  public void handleSaving(ActionEvent event) {
    
    boolean missing = false;
    String msg = "";
    if (!StringUtils.isNotEmpty(tfName.getText())) {
      msg += "Name is a mandatory field and cannot be omitted.\n";
      missing = true;
    }
    
    if (missing) {
      Utils.showWarningDialog("Mandatory field(s) missing", msg);
      return;
    }
    
    member = new Member(tfName.getText(), StringUtils.isNotBlank(tfFirstName.getText()) ? tfFirstName.getText() : "", StringUtils.isNotBlank(tfEmail.getText()) ? tfEmail.getText():"");
    
    dismiss();
    
  }
  
  @Override
  public Member create() throws IllegalStateException {
    if (!isCreatorReady()) {
      throw new IllegalStateException("Cannot create Member if not ready");
    }
    return member;
  }
  
  @Override
  public boolean isCreatorReady() {
    return member != null;
  }
  
  @Override
  protected void populateScene() {
    Label lblName = new Label("Name*");
    Label lblSurname = new Label("Surname");
    Label lblEmail = new Label("Email");
    
    tfName = new TextField();
    tfFirstName = new TextField();
    tfEmail = new TextField();
    
    grid.add(lblName, 0, 0);
    grid.add(tfName, 1, 0);
    
    grid.add(lblSurname, 0, 1);
    grid.add(tfFirstName, 1, 1);
    
    grid.add(lblEmail, 0, 2);
    grid.add(tfEmail, 1, 2);
    
    grid.add(buttons, 0, 4, 2, 1);
    
    setRoot(grid);
  }
}
