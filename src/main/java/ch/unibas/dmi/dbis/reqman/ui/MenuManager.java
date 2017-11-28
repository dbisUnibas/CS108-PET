package ch.unibas.dmi.dbis.reqman.ui;


import ch.unibas.dmi.dbis.reqman.data.Milestone;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class MenuManager {
  
  public static final String ITEM_NEW_COURSE = "itemNewCourse";
  public static final String ITEM_NEW_CAT = "itemNewCat";
  public static final String ITEM_NEW_GROUP = "itemNewGroup";
  public static final String ITEM_OPEN_COURSE = "itemOpenCourse";
  public static final String ITEM_OPEN_CAT = "itemOpenCat";
  public static final String ITEM_OPEN_GROUPS = "itemOpenGroups";
  public static final String ITEM_SAVE_COURSE = "itemSaveCourse";
  public static final String ITEM_SAVE_COURSE_AS = "itemSaveCourseAs";
  public static final String ITEM_SAVE_CAT = "itemSaveCat";
  public static final String ITEM_SAVE_CAT_AS = "itemSaveCatAs";
  public static final String ITEM_SAVE_GROUP = "itemSaveGroup";
  public static final String ITEM_SAVE_GROUP_AS = ITEM_SAVE_GROUP + "As";
  public static final String ITEM_EXPORT_CAT = "itemExportCat";
  public static final String ITEM_EXPORT_GROUPS = "itemExportGroups";
  public static final String ITEM_EXPORT_GROUP = "itemExportGroup";
  public static final String ITEM_QUIT = "itemQuit";
  public static final String ITEM_NEW_REQ = "itemNewReq";
  public static final String ITEM_NEW_MS = "itemNewMS";
  public static final String ITEM_MOD_CAT = "itemModCat";
  public static final String ITEM_MOD_REQ = "itemModReq";
  public static final String ITEM_MOD_MS = "itemModMS";
  public static final String ITEM_MOD_GROUP = "itemModGroup";
  public static final String ITEM_SHOW_OVERVIEW = "itemShowOverview";
  public static final String ITEM_EXPORT_OVERVIEW = "itemExportOverview";
  public static final String ITEM_EDITOR = "itemEditor";
  public static final String ITEM_EVALUATOR = "itemEvaluator";
  public static final String ITEM_PRESENTATION_MODE = "itemPresentation";
  public static final String CLEAR_GLOBAL_MS_KEY = "clear";
  private final static Logger LOGGER = LogManager.getLogger(MenuManager.class);
  private static MenuManager instance = null;
  private final ToggleGroup toggleMilestone = new ToggleGroup();
  private HashMap<String, MenuItem> menuItems = new HashMap<>();
  private HashMap<String, Menu> menus = new HashMap<>();
  private Menu menuFile;
  private Menu menuEdit;
  private Menu menuEvaluate;
  private Menu menuView;
  private Menu menuHelp;
  private Menu menuGlobalMilestone;
  
  private MenuItem itemNewCourse;
  private MenuItem itemNewCat;
  private MenuItem itemNewGroup;
  private MenuItem itemOpenCourse;
  private MenuItem itemOpenCat;
  private MenuItem itemOpenGroup;
  private MenuItem itemSaveCourse;
  private MenuItem itemSaveCat;
  private MenuItem itemSaveGroup;
  private MenuItem itemSaveCourseAs;
  private MenuItem itemSaveCatAs;
  private MenuItem itemSaveGroupAs;
  private MenuItem itemExportCat;
  private MenuItem itemExportGroups;
  private MenuItem itemExportGroup;
  private MenuItem itemQuit;
  private MenuItem itemNewReq;
  private MenuItem itemNewMS;
  private MenuItem itemModCat;
  private MenuItem itemModReq;
  private MenuItem itemModMS;
  private MenuItem itemModGroup;
  private MenuItem itemShowOverview;
  private MenuItem itemExportOverview;
  private MenuItem itemEditor;
  private MenuItem itemEvaluator;
  private MenuItem itemPresentation;
  private MenuBar menuBar = new MenuBar();
  private ArrayList<String> editorItems = new ArrayList<>();
  private ArrayList<String> evaluatorItems = new ArrayList<>();
  
  private ArrayList<String> catNeeded = new ArrayList<>();
  private ArrayList<String> groupNeeded = new ArrayList<>();
  
  private HashMap<String, String> activeKeyBindings = new HashMap<>();
  
  private MenuHandler handler = null;
  
  private MenuManager() {
    registerMenu("menuFile", menuFile = new Menu("File"));
    registerMenu("menuEdit", menuEdit = new Menu("Edit"));
    registerMenu("menuEvaluate", menuEvaluate = new Menu("Evaluate"));
    registerMenu("menuView", menuView = new Menu("View"));
    registerMenu("menuHelp", menuHelp = new Menu("Help"));
    
    registerMenuItem(ITEM_NEW_COURSE, itemNewCourse = new MenuItem("New Course..."));
    registerMenuItem(ITEM_NEW_CAT, itemNewCat = new MenuItem("New Catalogue..."));
    registerMenuItem(ITEM_NEW_GROUP, itemNewGroup = new MenuItem("New Group..."));
    catNeeded.add(ITEM_NEW_GROUP);
    registerMenuItem(ITEM_OPEN_COURSE, itemOpenCourse = new MenuItem("Open Course..."));
    registerMenuItem(ITEM_OPEN_CAT, itemOpenCat = new MenuItem("Open Catalogue..."));
    registerMenuItem(ITEM_OPEN_GROUPS, itemOpenGroup = new MenuItem("Open Group..."));
    catNeeded.add(ITEM_OPEN_GROUPS);
    
    registerEditorItem(ITEM_SAVE_COURSE, itemSaveCourse = new MenuItem("Save Course"));
    registerEditorItem(ITEM_SAVE_COURSE_AS, itemSaveCourseAs = new MenuItem("Save Course As..."));
    registerEditorItem(ITEM_SAVE_CAT, itemSaveCat = new MenuItem("Save Catalogue"), true);
    registerEvaluatorItem(ITEM_SAVE_GROUP, itemSaveGroup = new MenuItem("Save Group"), true);
    registerEditorItem(ITEM_SAVE_CAT_AS, itemSaveCatAs = new MenuItem("Save Catalogue As..."), true);
    registerEvaluatorItem(ITEM_SAVE_GROUP_AS, itemSaveGroupAs = new MenuItem("Save Group As..."), true);
    
    registerEditorItem(ITEM_EXPORT_CAT, itemExportCat = new MenuItem("Export Catalogue..."), true);
    registerEvaluatorItem(ITEM_EXPORT_GROUPS, itemExportGroups = new MenuItem("Export Groups..."), true);
    //registerEvaluatorItem(ITEM_EXPORT_GROUP, itemExportGroup = new MenuItem("Export Group..."), true); // TODO implement
    
    registerMenuItem(ITEM_QUIT, itemQuit = new MenuItem("Quit"));
    
    registerEditorItem(ITEM_NEW_REQ, itemNewReq = new MenuItem("New Requirement..."), true);
    registerEditorItem(ITEM_NEW_MS, itemNewMS = new MenuItem("New Milestone..."), true);
    registerEditorItem(ITEM_MOD_CAT, itemModCat = new MenuItem("Modify Catalogue..."), true);
    registerEditorItem(ITEM_MOD_REQ, itemModReq = new MenuItem("Modify Requirement..."), true);
    registerEditorItem(ITEM_MOD_MS, itemModMS = new MenuItem("Modify Milestone..."), true);
    registerEvaluatorItem(ITEM_MOD_GROUP, itemModGroup = new MenuItem("Modify Group"), true);
    
    registerEvaluatorItem(ITEM_SHOW_OVERVIEW, itemShowOverview = new MenuItem("Show Overview"), true);
    registerEvaluatorItem("menuGlobalMS", menuGlobalMilestone = new Menu("Set Global Milestone"), true);
    registerEvaluatorItem(ITEM_EXPORT_OVERVIEW, itemExportOverview = new MenuItem("Export Overview..."), true);
    
    
    registerMenuItem(ITEM_EDITOR, itemEditor = new MenuItem("Editor"));
    registerMenuItem(ITEM_EVALUATOR, itemEvaluator = new MenuItem("Evaluator"));
    registerMenuItem(ITEM_PRESENTATION_MODE, itemPresentation = new RadioMenuItem("Presentation Mode"));
    
    assembleMenus();
    menuBar.getMenus().addAll(menuFile, menuEdit, menuEvaluate, menuView, menuHelp);
    
    // TEMP
    menuHelp.setDisable(true);
    
    loadDefaultKeyBindings();
    
    setOnActionAll();
    setKeyBindings();
    
    disableAllButInitial();
  }
  
  public static MenuManager getInstance() {
    if (instance == null) {
      instance = new MenuManager();
    }
    return instance;
  }
  
  /**
   * Resets the menu each time.
   *
   * @param milestones Only a snapshot in time
   */
  public void setupGlobalMilestoneMenu(List<Milestone> milestones) {
    menuGlobalMilestone.getItems().clear();
    for (Milestone ms : milestones) {
      RadioMenuItem itemMilestone = new RadioMenuItem(ms.getName());
      itemMilestone.setUserData(ms);
      itemMilestone.setToggleGroup(toggleMilestone);
      menuGlobalMilestone.getItems().add(itemMilestone);
    }
    RadioMenuItem itemClearMilestone = new RadioMenuItem("Clear Global Milestone");
    itemClearMilestone.setUserData(CLEAR_GLOBAL_MS_KEY);
    itemClearMilestone.setToggleGroup(toggleMilestone);
    menuGlobalMilestone.getItems().add(0, itemClearMilestone);
  }
  
  public MenuHandler setMenuHandler(MenuHandler handler) {
    MenuHandler old = null;
    if (this.handler != null) {
      old = this.handler;
    }
    this.handler = handler;
    return old;
  }
  
  public MenuHandler getMenuHandler() {
    return handler;
  }
  
  public MenuBar getMenuBar() {
    return menuBar;
  }
  
  public void disableEditorItems() {
    setDisableForItems(editorItems, true);
  }
  
  public void enableEditorItems() {
    setDisableForItems(editorItems, false);
  }
  
  public void disableEvaluatorItems() {
    setDisableForItems(evaluatorItems, true);
  }
  
  public void enableEvaluaotrItems() {
    setDisableForItems(evaluatorItems, false);
  }
  
  public void disableCatalogueNeeded() {
    setDisableForItems(catNeeded, true);
  }
  
  public void enableCatalogueNeeded() {
    setDisableForItems(catNeeded, false);
  }
  
  public void disableGroupNeeded() {
    setDisableForItems(groupNeeded, true);
  }
  
  public void enableGroupNeeded() {
    LOGGER.traceEntry();
    setDisableForItems(groupNeeded, false);
  }
  
  public void disableAllButInitial() {
    disableCatalogueNeeded();
    disableEditorItems();
    disableGroupNeeded();
    disableEvaluatorItems();
  }
  
  private void loadDefaultKeyBindings() {
    activeKeyBindings.put(ITEM_OPEN_CAT, "Ctrl+L");
    activeKeyBindings.put(ITEM_SAVE_CAT, "Alt+S");
    activeKeyBindings.put(ITEM_SAVE_CAT_AS, "Alt+Shift+S");
    activeKeyBindings.put(ITEM_OPEN_GROUPS, "Ctrl+O");
    activeKeyBindings.put(ITEM_SAVE_GROUP, "Ctrl+S");
    activeKeyBindings.put(ITEM_SAVE_GROUP_AS, "Ctrl+Shift+S");
    activeKeyBindings.put(ITEM_EXPORT_CAT, "Alt+E");
    activeKeyBindings.put(ITEM_EXPORT_GROUPS, "Ctrl+E");
  }
  
  private void setOnActionAll() {
    menuItems.values().forEach(mi -> mi.setOnAction(this::handle));
  }
  
  private void setKeyBindings() {
    activeKeyBindings.forEach((miKey, combination) -> {
      MenuItem mi = menuItems.get(miKey);
      if (mi != null) {
        mi.setAccelerator(KeyCombination.keyCombination(combination));
      }
    });
  }
  
  private void handle(ActionEvent event) {
    if (event.getSource() != null) {
      if (event.getSource() instanceof MenuItem) {
        MenuItem mi = (MenuItem) event.getSource();
        if (mi.getUserData() != null && mi.getUserData() instanceof String) {
          String key = (String) mi.getUserData();
          switch (key) {
            case ITEM_NEW_COURSE:
              handler.handleNewCourse(event);
              break;
            case ITEM_NEW_CAT:
              handler.handleNewCatalogue(event);
              break;
            case ITEM_NEW_GROUP:
              handler.handleNewGroup(event);
              break;
            case ITEM_OPEN_COURSE:
              handler.handleOpenCourse(event);
              break;
            case ITEM_OPEN_CAT:
              handler.handleOpenCat(event);
              break;
            case ITEM_OPEN_GROUPS:
              handler.handleOpenGroups(event);
              break;
            case ITEM_SAVE_COURSE:
              handler.handleSaveCourse(event);
              break;
            case ITEM_SAVE_CAT:
              handler.handleSaveCat(event);
              break;
            case ITEM_SAVE_GROUP:
              handler.handleSaveGroup(event);
              break;
            case ITEM_SAVE_COURSE_AS:
              handler.handleSaveCourseAs(event);
              break;
            case ITEM_SAVE_CAT_AS:
              handler.handleSaveCatAs(event);
              break;
            case ITEM_SAVE_GROUP_AS:
              handler.handleSaveGroupAs(event);
              break;
            case ITEM_EXPORT_CAT:
              handler.handleExportCat(event);
              break;
            case ITEM_EXPORT_GROUP:
              handler.handleExportGroup(event);
              break;
            case ITEM_EXPORT_GROUPS:
              handler.handleExportGroups(event);
              break;
            case ITEM_QUIT:
              handler.handleQuit(event);
              break;
            case ITEM_NEW_REQ:
              handler.handleNewReq(event);
              break;
            case ITEM_NEW_MS:
              handler.handleNewMS(event);
              break;
            case ITEM_MOD_CAT:
              handler.handleModCat(event);
              break;
            case ITEM_MOD_REQ:
              handler.handleModReq(event);
              break;
            case ITEM_MOD_MS:
              handler.handleModMS(event);
              break;
            case ITEM_MOD_GROUP:
              handler.handleModGroup(event);
              break;
            case ITEM_SHOW_OVERVIEW:
              handler.handleShowOverview(event);
              break;
            case ITEM_EDITOR:
              handler.handleShowEditor(event);
              break;
            case ITEM_EVALUATOR:
              handler.handleShowEvaluator(event);
              break;
            case ITEM_PRESENTATION_MODE:
              handler.handlePresentationMode(event);
              break;
            case ITEM_EXPORT_OVERVIEW:
              handler.handleExportOverview(event);
              break;
            default:
              // Silently ignoring -> may log issue?
          }
        }
      }
    }
  }
  
  private void assembleMenus() {
    menuFile.getItems().addAll(
        itemNewCourse,
        itemNewCat,
        itemNewGroup,
        new SeparatorMenuItem(),
        itemOpenCourse, itemOpenCat, itemOpenGroup,
        new SeparatorMenuItem(),
        itemSaveCourse, itemSaveCourseAs, itemSaveCat, itemSaveCatAs, itemSaveGroup, itemSaveGroupAs,
        new SeparatorMenuItem(),
        itemExportCat, itemExportGroups/*,itemExportGroup /*TODO implement*/,
        new SeparatorMenuItem(),
        itemQuit
    );
    menuEdit.getItems().addAll(itemNewReq, itemNewMS,
        new SeparatorMenuItem(),
        itemModCat,
        itemModReq,
        itemModMS,
        itemModGroup
    );
    
    // The set-milestone-forall-groups menu related handling
    toggleMilestone.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) -> {
            /*
            Lots of trace logging to understand what is going on
             */
      
      if (ov != null) {
        LOGGER.trace("Obesrvable Value: " + ov.toString());
        if (ov.getValue() instanceof RadioMenuItem) {
          RadioMenuItem item = (RadioMenuItem) ov.getValue();
          if (item != null && item.getUserData() != null) {
            if (item.getUserData() instanceof String && ((String) item.getUserData()).equals("clear")) {
              handler.resetGlobalMilestoneChoice();
              return;
            }
          }
        }
      }
      if (LOGGER.getLevel().equals(Level.TRACE)) {
        // Print stuff only if on trace:
        if (newToggle != null) {
          if (newToggle.getUserData() instanceof Milestone) {
            Milestone newMS = (Milestone) newToggle.getUserData();
            LOGGER.trace("newMS: " + newMS.getName());
          } else {
            LOGGER.trace("newToggle: " + newToggle.toString());
          }
          
        }
        if (oldToggle != null) {
          if (oldToggle.getUserData() instanceof Milestone) {
            Milestone oldMS = (Milestone) oldToggle.getUserData();
            LOGGER.trace("oldMS: " + oldMS.getName());
          } else {
            LOGGER.trace("oldToggle: " + oldToggle.toString());
          }
          
        }
      }


            /*
            Conclusion: Only if *newly* selected the event is fired and thus handled in here.
             */
      if (toggleMilestone.getSelectedToggle() != null && toggleMilestone.getSelectedToggle().getUserData() instanceof Milestone) {
        Milestone ms = (Milestone) toggleMilestone.getSelectedToggle().getUserData();
        LOGGER.debug("Selected: " + ms.getName());
        handler.setGlobalMilestoneChoice(ms);
      }
    });
    
    menuEvaluate.getItems().addAll(itemShowOverview, itemExportOverview, new SeparatorMenuItem(), menuGlobalMilestone);
    
    menuView.getItems().addAll(itemEditor, itemEvaluator, new SeparatorMenuItem(), itemPresentation);
  }
  
  private void registerEditorItem(String key, MenuItem item) {
    registerEditorItem(key, item, false);
  }
  
  private void registerEditorItem(String key, MenuItem item, boolean catNeeded) {
    registerMenuItem(key, item);
    editorItems.add(key);
    if (catNeeded) {
      this.catNeeded.add(key);
    }
  }
  
  private void registerEvaluatorItem(String key, MenuItem item, boolean groupNeeded) {
    registerMenuItem(key, item);
    evaluatorItems.add(key);
    if (groupNeeded) {
      this.groupNeeded.add(key);
    }
  }
  
  private void registerEvaluatorItem(String key, MenuItem item) {
    registerEvaluatorItem(key, item, false);
  }
  
  private void registerMenuItem(String key, MenuItem item) {
    menuItems.put(key, item);
    item.setUserData(key);
  }
  
  private void registerMenu(String key, Menu menu) {
    menus.put(key, menu);
    menu.setUserData(key);
  }
  
  private void setDisableForItems(List<String> keyProvider, boolean disable) {
    keyProvider.forEach(key -> {
      LOGGER.trace(":setDisableForItems - " + String.format("Setting %b for %s", disable, key));
      menuItems.get(key).setDisable(disable);
    });
  }
  
}
