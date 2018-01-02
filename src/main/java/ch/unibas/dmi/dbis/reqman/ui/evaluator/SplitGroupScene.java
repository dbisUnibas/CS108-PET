package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Member;
import ch.unibas.dmi.dbis.reqman.ui.common.AbstractVisualCreator;
import ch.unibas.dmi.dbis.reqman.ui.common.Utils;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class SplitGroupScene extends AbstractVisualCreator<Group> {
  private static final Logger LOGGER = LogManager.getLogger();
  
  private static final int MEMBERS_START_ROW_INDEX = 3;
  private Label titleLbl;
  private Label fromLbl;
  private Label toLbl;
  private Label membersLbl;
  private ComboBox<Group> groupCB;
  private TextField newGroupTF;
  private int nbMembers = 0;
  private List<ToggleButton> keepButtons;
  private List<ToggleButton> goButtons;
  private List<ToggleGroup> toggleGroups;
  private List<Label> namesList;
  private List<Member> members;
  
  private Group split = null;
  
  public SplitGroupScene(){
    LOGGER.debug("Creation of SplitGroupScene");
    populateScene();
//    grid.setPrefWidth(800);
//    grid.setPrefHeight(600);
    
    groupCB.getSelectionModel().selectFirst();
  }
  
  @Override
  public String getPromptTitle() {
    return "Split Group";
  }
  
  @Override
  public void handleSaving(ActionEvent event) {
    String name = newGroupTF.getText();
    if (StringUtils.isBlank(name)) {
      Utils.showErrorDialog("Missing new group name", "When splitting a group, the resulting group needs a name.");
      return;
    }
    List<Member> keeping = new ArrayList<>();
    List<Member> going = new ArrayList<>();
    
    for (int i = 0; i < nbMembers; i++) {
      if (keepButtons.get(i).isSelected()) {
        keeping.add(members.get(i));
      } else if (goButtons.get(i).isSelected()) {
        going.add(members.get(i));
      }
    }
    boolean noSplit = false;
    if (going.isEmpty()) {
      noSplit = true;
      
    } else if (keeping.isEmpty()) {
      noSplit = true;
    }
    
    if (noSplit) {
      Utils.showErrorDialog("Empty split", "The split configuration is not a splitting.\n" +
          "To split a group, some members have to go and some are kept, thus two groups are formed from one.");
      return;
    }
    
    Group old = groupCB.getSelectionModel().getSelectedItem();
    old.setMembers(keeping);
    split = EntityController.getInstance().copyGroup(old, name, going.toArray(new Member[0]));
    dismiss();
  }
  
  @Override
  public Group create() throws IllegalStateException {
    return split;
  }
  
  @Override
  public boolean isCreatorReady() {
    return split != null;
  }
  
  @Override
  protected void populateScene() {
    initComps();
    grid.addRow(0, titleLbl);
    grid.addRow(1, fromLbl, groupCB, toLbl, newGroupTF);
    grid.addRow(2, membersLbl);
    
    grid.add(buttons, 1,10,4,1);
  }
  
  private void initComps() {
    
    keepButtons = new ArrayList<>();
    goButtons = new ArrayList<>();
    toggleGroups = new ArrayList<>();
    namesList = new ArrayList<>();
    members = new ArrayList<>();
    
    titleLbl = new Label("Split Group");
    fromLbl = new Label("from");
    toLbl = new Label("to");
    membersLbl = new Label("Members");
    groupCB = new ComboBox<>();
    groupCB.setButtonCell(new GroupCell());
    groupCB.setCellFactory( value -> new GroupCell());
    groupCB.setItems(EntityController.getInstance().groupList());
    LOGGER.debug("GroupComboBox contains: {}", groupCB.getItems().stream().<String>map(Group::getName).collect(Collectors.toList()));
    groupCB.getSelectionModel().selectedItemProperty().addListener((this::handleSelection));
    newGroupTF = new TextField();
  }
  
  private void handleSelection(ObservableValue<? extends Group> observable, Group oldValue, Group newValue) {
    addMembersToDisplay(newValue.getMembers());
  }
  
  private void addMembersToDisplay(Member[] members) {
    Arrays.asList(members).forEach(member -> {
      LOGGER.debug("Preparing {} for display", member);
      ToggleButton keepTB = new ToggleButton("keep");
      ToggleButton goTB = new ToggleButton("move");
      ToggleGroup toggleGroup = new ToggleGroup();
      keepTB.setToggleGroup(toggleGroup);
      goTB.setToggleGroup(toggleGroup);
      keepButtons.add(keepTB);
      goButtons.add(goTB);
      toggleGroups.add(toggleGroup);
      Label lbl = new Label(member.getFirstName() + (StringUtils.isNotBlank(member.getName()) ? member.getName() : ""));
      namesList.add(lbl);
      this.members.add(member);
      nbMembers++;
    });
    for (int i = 0; i < nbMembers; i++) {
      grid.add(namesList.get(i), 0, MEMBERS_START_ROW_INDEX + i);
      grid.add(keepButtons.get(i), 1, MEMBERS_START_ROW_INDEX + i);
      grid.add(goButtons.get(i), 3, MEMBERS_START_ROW_INDEX + i);
      LOGGER.debug("Added {} to display: {}", this.members.get(i), namesList.get(i).getText());
    }
  }
  
  
}
