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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Main menu management unit.
 *
 * This class manages all of the menu handles, it also builds the menu and loads shortcuts.
 *
 * Each menu item has a userdata string which is in the form of <code>menu[.submenu].name.item</code>.
 * E.g. the menu item <i>Quit</i> under the menu <i>File</i> has the key <code>file.quit.item</code>
 *
 * @author loris.sauter
 */
public class MenuManager {
  
  public static final String MENU_SUFFIX = "menu";
  public static final String ITEM_SUFFIX = "item";
  public static final String KEY_SEPARATOR = ".";
  public static final String MENU_FILE_PREFIX = "file";
  public static final String MENU_NEW_PREFIX = MENU_FILE_PREFIX +KEY_SEPARATOR+"new";
  public static final String MENU_OPEN_PREFIX = MENU_FILE_PREFIX +KEY_SEPARATOR+"open";
  public static final String MENU_SAVE_PREFIX = MENU_FILE_PREFIX +KEY_SEPARATOR+"save";
  public static final String MENU_EXPORT_PREFIX = MENU_FILE_PREFIX + KEY_SEPARATOR+"export";
  public static final String MENU_EDIT_PREFIX = "edit";
  public static final String MENU_MODIFY_PREFIX ="modify";
  public static final String MENU_VIEW_PREFIX ="view";
  public static final String MENU_MODE_PREFIX = "mode";
  public static final String MENU_HELP_PREFIX = "help";
  
  
  public static final String ITEM_NEW_COURSE = MENU_NEW_PREFIX+KEY_SEPARATOR+"course"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_NEW_CAT = MENU_NEW_PREFIX+KEY_SEPARATOR+"catalogue"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_NEW_GROUP = MENU_NEW_PREFIX+KEY_SEPARATOR+"group"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_OPEN_COURSE = MENU_OPEN_PREFIX+KEY_SEPARATOR+"course"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_OPEN_CAT = MENU_OPEN_PREFIX+KEY_SEPARATOR+"catalogue"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_OPEN_GROUPS = MENU_OPEN_PREFIX+KEY_SEPARATOR+"groups"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_SAVE_ALL = MENU_SAVE_PREFIX+KEY_SEPARATOR+"all"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_SAVE_COURSE = MENU_SAVE_PREFIX+KEY_SEPARATOR+"course"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_SAVE_COURSE_AS = MENU_SAVE_PREFIX+KEY_SEPARATOR+"course-as"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_SAVE_CAT = MENU_SAVE_PREFIX+KEY_SEPARATOR+"catalogue"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_SAVE_CAT_AS = MENU_SAVE_PREFIX+KEY_SEPARATOR+"catalogue-as"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_SAVE_GROUP = MENU_SAVE_PREFIX+KEY_SEPARATOR+"group"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_SAVE_GROUP_AS = MENU_SAVE_PREFIX+KEY_SEPARATOR+"group-as"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_EXPORT_CAT = MENU_EXPORT_PREFIX+KEY_SEPARATOR+"catalogue"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_EXPORT_GROUPS = MENU_EXPORT_PREFIX+KEY_SEPARATOR+"groups"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_EXPORT_GROUP = MENU_EXPORT_PREFIX+KEY_SEPARATOR+"group-single"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_QUIT = MENU_FILE_PREFIX+KEY_SEPARATOR+"quit"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_NEW_REQ = MENU_NEW_PREFIX+KEY_SEPARATOR+"requirement"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_NEW_MS = MENU_NEW_PREFIX+KEY_SEPARATOR+"milestone"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_MOD_CAT = MENU_MODIFY_PREFIX+KEY_SEPARATOR+"catalogue"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_MOD_REQ = MENU_MODIFY_PREFIX+KEY_SEPARATOR+"requirement"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_MOD_MS = MENU_MODIFY_PREFIX+KEY_SEPARATOR+"milestone"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_MOD_GROUP = MENU_MODIFY_PREFIX+KEY_SEPARATOR+"group"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_SPLIT_GROUP = MENU_EDIT_PREFIX+KEY_SEPARATOR+"split"+KEY_SEPARATOR+ITEM_SUFFIX;
  @Deprecated public static final String ITEM_SHOW_OVERVIEW = MENU_VIEW_PREFIX+KEY_SEPARATOR+"catalogue-overview"+KEY_SEPARATOR+ITEM_SUFFIX;
  @Deprecated public static final String ITEM_EXPORT_OVERVIEW = MENU_VIEW_PREFIX+KEY_SEPARATOR+"export-overview"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_EDITOR = MENU_MODE_PREFIX+KEY_SEPARATOR+"editor"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_EVALUATOR = MENU_MODE_PREFIX+KEY_SEPARATOR+"evaluator"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_SHOW_FILTER_BAR_EDIOR = MENU_VIEW_PREFIX+KEY_SEPARATOR+"filterbar"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_CLEAR_FILTER_EDITOR = MENU_VIEW_PREFIX+KEY_SEPARATOR+"clearfilter"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_SHOW_CATALOGUE_STATISTICS = MENU_VIEW_PREFIX+KEY_SEPARATOR+"catalogue-stats"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_SHOW_GROUP_STATISTICS = MENU_VIEW_PREFIX+KEY_SEPARATOR+"group-stats"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_PRESENTATION_MODE = MENU_VIEW_PREFIX+KEY_SEPARATOR+"presentation"+KEY_SEPARATOR+ITEM_SUFFIX;
  @Deprecated public static final String CLEAR_GLOBAL_MS_KEY = "clear";
  public static final String ITEM_IMPORT = MENU_FILE_PREFIX+KEY_SEPARATOR+"import"+KEY_SEPARATOR+ITEM_SUFFIX;
  
  private final static Logger LOGGER = LogManager.getLogger(MenuManager.class);
  private static MenuManager instance = null;
  private final ToggleGroup toggleMilestone = new ToggleGroup();
  private final MenuItem itemSplitGroup;
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
  private MenuItem itemEditorFilterShow;
  private MenuItem itemEditorFilterClear;
  private MenuItem itemShowCatalogueStatistics;
  private MenuItem itemShowGroupStatistics;
  private MenuItem itemPresentation;
  private MenuItem itemImport;
  private MenuBar menuBar = new MenuBar();
  private ArrayList<String> editorItems = new ArrayList<>();
  private ArrayList<String> evaluatorItems = new ArrayList<>();
  
  private ArrayList<String> catNeeded = new ArrayList<>();
  private ArrayList<String> groupNeeded = new ArrayList<>();
  private ArrayList<String> openItems = new ArrayList<>();
  
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
    openItems.add(ITEM_OPEN_COURSE);
    registerMenuItem(ITEM_OPEN_CAT, itemOpenCat = new MenuItem("Open Catalogue..."));
    openItems.add(ITEM_OPEN_CAT);
    registerMenuItem(ITEM_OPEN_GROUPS, itemOpenGroup = new MenuItem("Open Group..."));
    registerMenuItem(ITEM_IMPORT, itemImport = new MenuItem("Import Catalogue..."));
    openItems.add(ITEM_IMPORT);
    openItems.add(ITEM_OPEN_GROUPS);
    catNeeded.add(ITEM_OPEN_GROUPS);
    
    registerEditorItem(ITEM_SAVE_COURSE, itemSaveCourse = new MenuItem("Save Course"));
    registerEditorItem(ITEM_SAVE_COURSE_AS, itemSaveCourseAs = new MenuItem("Save Course As..."));
    registerEditorItem(ITEM_SAVE_CAT, itemSaveCat = new MenuItem("Save Catalogue"), true);
    registerEvaluatorItem(ITEM_SAVE_GROUP, itemSaveGroup = new MenuItem("Save Group"), true);
    registerEditorItem(ITEM_SAVE_CAT_AS, itemSaveCatAs = new MenuItem("Save Catalogue As..."), true);
    registerEvaluatorItem(ITEM_SAVE_GROUP_AS, itemSaveGroupAs = new MenuItem("Save Group As..."), true);
    
    registerEditorItem(ITEM_EXPORT_CAT, itemExportCat = new MenuItem("Export Catalogue..."), true);
    registerEvaluatorItem(ITEM_EXPORT_GROUPS, itemExportGroups = new MenuItem("Export Groups..."), true);
    registerEvaluatorItem(ITEM_EXPORT_GROUP, itemExportGroup = new MenuItem("Export Active Group..."), true);
    
    registerMenuItem(ITEM_QUIT, itemQuit = new MenuItem("Quit"));
    
    registerEditorItem(ITEM_NEW_REQ, itemNewReq = new MenuItem("New Requirement..."), true);
    registerEditorItem(ITEM_NEW_MS, itemNewMS = new MenuItem("New Milestone..."), true);
    registerEditorItem(ITEM_MOD_CAT, itemModCat = new MenuItem("Modify Catalogue..."), true);
    registerEditorItem(ITEM_MOD_REQ, itemModReq = new MenuItem("Modify Requirement..."), true);
    registerEditorItem(ITEM_MOD_MS, itemModMS = new MenuItem("Modify Milestone..."), true);
    registerEvaluatorItem(ITEM_MOD_GROUP, itemModGroup = new MenuItem("Modify Group"), true);
    registerEvaluatorItem(ITEM_SPLIT_GROUP, itemSplitGroup = new MenuItem("Split Group..."), true);
    
    registerEvaluatorItem(ITEM_SHOW_OVERVIEW, itemShowOverview = new MenuItem("Show Overview"), true);
    registerEvaluatorItem("menuGlobalMS", menuGlobalMilestone = new Menu("Set Global Milestone"), true);
    registerEvaluatorItem(ITEM_EXPORT_OVERVIEW, itemExportOverview = new MenuItem("Export Overview..."), true);
    
    
    registerMenuItem(ITEM_EDITOR, itemEditor = new MenuItem("Editor"));
    registerMenuItem(ITEM_EVALUATOR, itemEvaluator = new MenuItem("Evaluator"));
    registerMenuItem(ITEM_SHOW_FILTER_BAR_EDIOR, itemEditorFilterShow = new MenuItem("Show Filter Bar"));
    registerMenuItem(ITEM_CLEAR_FILTER_EDITOR, itemEditorFilterClear = new MenuItem("Clear Filter"));
    registerMenuItem(ITEM_SHOW_CATALOGUE_STATISTICS, itemShowCatalogueStatistics = new MenuItem("Catalogue Overview..."));
    registerMenuItem(ITEM_SHOW_GROUP_STATISTICS, itemShowGroupStatistics = new MenuItem("Groups Overview..."));
    catNeeded.addAll(Arrays.asList(ITEM_CLEAR_FILTER_EDITOR, ITEM_SHOW_FILTER_BAR_EDIOR, ITEM_SHOW_CATALOGUE_STATISTICS));
    groupNeeded.add(ITEM_SHOW_GROUP_STATISTICS);
    
    registerMenuItem(ITEM_PRESENTATION_MODE, itemPresentation = new RadioMenuItem("Presentation Mode"));
    
    assembleMenus();
    menuBar.getMenus().addAll(menuFile, menuEdit, menuEvaluate, menuView, menuHelp);
    
    // TEMP // TODO fix menu / implement stuff
    disableUnused();
    
    loadDefaultKeyBindings();
    
    setOnActionAll();
    setKeyBindings();
    
    disableAllButInitial();
  }
  
  private void disableUnused(){
    menuHelp.setDisable(true);
    menuEvaluate.setDisable(true);
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
    // TODO
    disableUnused();
  }
  
  public void disableEvaluatorItems() {
    setDisableForItems(evaluatorItems, true);
  }
  
  public void enableEvaluaotrItems() {
    setDisableForItems(evaluatorItems, false);
    // TODO
    disableUnused();
  }
  
  public void disableCatalogueNeeded() {
    setDisableForItems(catNeeded, true);
  }
  
  public void enableCatalogueNeeded() {
    setDisableForItems(catNeeded, false);
    // TODO
    disableUnused();
  }
  
  public void disableGroupNeeded() {
    setDisableForItems(groupNeeded, true);
  }
  
  public void enableGroupNeeded() {
    LOGGER.traceEntry();
    setDisableForItems(groupNeeded, false);
    
    // TODO
    disableUnused();
  }
  
  public void disableAllButInitial() {
    disableCatalogueNeeded();
    disableEditorItems();
    disableGroupNeeded();
    disableEvaluatorItems();
  }
  
  public void enableOpenItems() {
    setDisableForItems(openItems, false);
    
    // TODO
    disableUnused();
  }
  
  private void loadDefaultKeyBindings() {
    activeKeyBindings.put(ITEM_OPEN_CAT, "Ctrl+L");
    activeKeyBindings.put(ITEM_SAVE_CAT, "Alt+S");
    activeKeyBindings.put(ITEM_SAVE_CAT_AS, "Alt+Shift+S");
    activeKeyBindings.put(ITEM_NEW_GROUP, "Ctrl+G");
    activeKeyBindings.put(ITEM_OPEN_GROUPS, "Ctrl+O");
    activeKeyBindings.put(ITEM_SAVE_GROUP, "Ctrl+S");
    activeKeyBindings.put(ITEM_SAVE_GROUP_AS, "Ctrl+Shift+S");
    activeKeyBindings.put(ITEM_EXPORT_CAT, "Alt+E");
    activeKeyBindings.put(ITEM_EXPORT_GROUPS, "Ctrl+E");
    activeKeyBindings.put(ITEM_SHOW_FILTER_BAR_EDIOR, "Ctrl+F");
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
            case ITEM_CLEAR_FILTER_EDITOR:
              handler.handleClearFilter(event);
              break;
            case ITEM_SHOW_FILTER_BAR_EDIOR:
              handler.handleShowFilterBar(event);
              break;
            case ITEM_SPLIT_GROUP:
              handler.handleSplitGroup(event);
              break;
            case ITEM_SHOW_CATALOGUE_STATISTICS:
              handler.handleCatalogueStatistics(event);
              break;
            case ITEM_IMPORT:
              handler.handleImport(event);
              break;
            case ITEM_SHOW_GROUP_STATISTICS:
              handler.handleGroupStatistics(event);
              break;
            default:
              // Silently ignoring -> may log issue?
          }
        }
      }
    }
  }
  
  private void assembleMenus() {
    /* === FILE MENU === */
    menuFile.getItems().addAll(
        itemNewCourse,
        itemNewCat,
        itemNewGroup,
        new SeparatorMenuItem(),
        itemOpenCourse, itemOpenCat, itemOpenGroup,
        new SeparatorMenuItem(),
        itemSaveCourse, itemSaveCourseAs, itemSaveCat, itemSaveCatAs, itemSaveGroup, itemSaveGroupAs,
        new SeparatorMenuItem(),
        itemExportCat, itemExportGroups,itemExportGroup,
        new SeparatorMenuItem(),
        itemImport,
        new SeparatorMenuItem(),
        itemQuit
    );
    /* === EDIT MENU === */
    menuEdit.getItems().addAll(itemNewReq, itemNewMS,
        new SeparatorMenuItem(),
        itemModCat,
        itemModReq,
        itemModMS,
        itemModGroup,
        itemSplitGroup
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
    
    /* === EVALUATE MENU ===*/
    menuEvaluate.getItems().addAll(itemShowOverview, itemExportOverview, new SeparatorMenuItem(), menuGlobalMilestone);
    
    /* === VIEW MENU === */
    menuView.getItems().addAll(
        itemEditor,
        itemEvaluator,
        new SeparatorMenuItem(),
        itemEditorFilterShow,
        itemEditorFilterClear,
        new SeparatorMenuItem(),
        itemShowCatalogueStatistics,
        itemShowGroupStatistics,
        new SeparatorMenuItem(),
        itemPresentation);
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
