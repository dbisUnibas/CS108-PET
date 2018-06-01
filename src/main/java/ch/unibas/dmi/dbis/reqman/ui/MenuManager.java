package ch.unibas.dmi.dbis.reqman.ui;


import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Main menu management unit.
 * <p>
 * This class manages all of the menu handles, it also builds the menu and loads shortcuts.
 * <p>
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
  public static final String MENU_NEW_PREFIX = MENU_FILE_PREFIX + KEY_SEPARATOR + "new";
  public static final String MENU_OPEN_PREFIX = MENU_FILE_PREFIX + KEY_SEPARATOR + "open";
  public static final String MENU_SAVE_PREFIX = MENU_FILE_PREFIX + KEY_SEPARATOR + "save";
  public static final String MENU_EXPORT_PREFIX = MENU_FILE_PREFIX + KEY_SEPARATOR + "export";
  public static final String MENU_EDIT_PREFIX = "edit";
  public static final String MENU_MODIFY_PREFIX = "modify";
  public static final String MENU_VIEW_PREFIX = "view";
  public static final String MENU_MODE_PREFIX = "mode";
  public static final String MENU_HELP_PREFIX = "help";
  
  public static final String MENU_FILE = MENU_FILE_PREFIX + KEY_SEPARATOR + MENU_SUFFIX;
  public static final String MENU_EDIT = MENU_EDIT_PREFIX + KEY_SEPARATOR + MENU_SUFFIX;
  public static final String MENU_VIEW = MENU_VIEW_PREFIX + KEY_SEPARATOR + MENU_SUFFIX;
  public static final String MENU_MODE = MENU_MODE_PREFIX + KEY_SEPARATOR + MENU_SUFFIX;
  public static final String MENU_HELP = MENU_HELP_PREFIX + KEY_SEPARATOR + MENU_SUFFIX;
  
  public static final String MENU_NEW = MENU_FILE_PREFIX + KEY_SEPARATOR + MENU_NEW_PREFIX + KEY_SEPARATOR + MENU_SUFFIX;
  public static final String MENU_OPEN = MENU_FILE_PREFIX + KEY_SEPARATOR + MENU_OPEN_PREFIX + KEY_SEPARATOR + MENU_SUFFIX;
  public static final String MENU_SAVE = MENU_FILE_PREFIX + KEY_SEPARATOR + MENU_SAVE_PREFIX + KEY_SEPARATOR + MENU_SUFFIX;
  public static final String MENU_EXPORT = MENU_FILE_PREFIX + KEY_SEPARATOR + MENU_EXPORT_PREFIX + KEY_SEPARATOR + MENU_SUFFIX;
  public static final String MENU_MODIFY = MENU_EDIT_PREFIX + KEY_SEPARATOR + MENU_MODIFY_PREFIX + KEY_SEPARATOR + MENU_SUFFIX;
  
  
  public static final String ITEM_NEW_COURSE = MENU_NEW_PREFIX + KEY_SEPARATOR + "course" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_NEW_CAT = MENU_NEW_PREFIX + KEY_SEPARATOR + "catalogue" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_NEW_GROUP = MENU_NEW_PREFIX + KEY_SEPARATOR + "group" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_OPEN_COURSE = MENU_OPEN_PREFIX + KEY_SEPARATOR + "course" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_OPEN_CAT = MENU_OPEN_PREFIX + KEY_SEPARATOR + "catalogue" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_OPEN_GROUPS = MENU_OPEN_PREFIX + KEY_SEPARATOR + "groups" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_SAVE_ALL = MENU_SAVE_PREFIX + KEY_SEPARATOR + "all" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_SAVE_COURSE = MENU_SAVE_PREFIX + KEY_SEPARATOR + "course" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_SAVE_COURSE_AS = MENU_SAVE_PREFIX + KEY_SEPARATOR + "course-as" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_SAVE_CAT = MENU_SAVE_PREFIX + KEY_SEPARATOR + "catalogue" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_SAVE_CAT_AS = MENU_SAVE_PREFIX + KEY_SEPARATOR + "catalogue-as" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_SAVE_GROUP = MENU_SAVE_PREFIX + KEY_SEPARATOR + "group" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_SAVE_GROUP_AS = MENU_SAVE_PREFIX + KEY_SEPARATOR + "group-as" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_EXPORT_CAT = MENU_EXPORT_PREFIX + KEY_SEPARATOR + "catalogue" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_EXPORT_GROUPS = MENU_EXPORT_PREFIX + KEY_SEPARATOR + "groups" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_EXPORT_GROUP = MENU_EXPORT_PREFIX + KEY_SEPARATOR + "group-single" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_QUIT = MENU_FILE_PREFIX + KEY_SEPARATOR + "quit" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_NEW_REQ = MENU_NEW_PREFIX + KEY_SEPARATOR + "requirement" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_NEW_MS = MENU_NEW_PREFIX + KEY_SEPARATOR + "milestone" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_MOD_COURSE = MENU_MODIFY_PREFIX+KEY_SEPARATOR+"course"+KEY_SEPARATOR+ITEM_SUFFIX;
  public static final String ITEM_MOD_CAT = MENU_MODIFY_PREFIX + KEY_SEPARATOR + "catalogue" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_MOD_REQ = MENU_MODIFY_PREFIX + KEY_SEPARATOR + "requirement" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_MOD_MS = MENU_MODIFY_PREFIX + KEY_SEPARATOR + "milestone" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_MOD_GROUP = MENU_MODIFY_PREFIX + KEY_SEPARATOR + "group" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_SPLIT_GROUP = MENU_EDIT_PREFIX + KEY_SEPARATOR + "split" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_EDITOR = MENU_MODE_PREFIX + KEY_SEPARATOR + "editor" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_EVALUATOR = MENU_MODE_PREFIX + KEY_SEPARATOR + "evaluator" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_SHOW_FILTERBAR = MENU_VIEW_PREFIX + KEY_SEPARATOR + "filterbar" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_CLEAR_FILTERS = MENU_VIEW_PREFIX + KEY_SEPARATOR + "clearfilter" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_SHOW_CATALOGUE_STATISTICS = MENU_VIEW_PREFIX + KEY_SEPARATOR + "catalogue-stats" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_SHOW_GROUP_STATISTICS = MENU_VIEW_PREFIX + KEY_SEPARATOR + "group-stats" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_PRESENTATION_MODE = MENU_VIEW_PREFIX + KEY_SEPARATOR + "presentation" + KEY_SEPARATOR + ITEM_SUFFIX;
  
  public static final String ITEM_ABOUT = MENU_HELP_PREFIX + KEY_SEPARATOR + "about" + KEY_SEPARATOR + ITEM_SUFFIX;
  public static final String ITEM_HELP = MENU_HELP_PREFIX + KEY_SEPARATOR + "help" + KEY_SEPARATOR + ITEM_SUFFIX;
  
  public static final String ITEM_IMPORT = MENU_FILE_PREFIX + KEY_SEPARATOR + "import" + KEY_SEPARATOR + ITEM_SUFFIX;
  
  private final static Logger LOGGER = LogManager.getLogger(MenuManager.class);
  private static MenuManager instance = null;
  
  private MenuItem itemSplitGroup;
  
  private HashMap<String, MenuItem> menuItems = new HashMap<>();
  private HashMap<String, Menu> menus = new HashMap<>();
  
  private Menu menuFile;
  
  private Menu menuNew;
  private Menu menuOpen;
  private Menu menuSave;
  private Menu menuExport;
  private Menu menuModify;
  
  private Menu menuEdit;
  private Menu menuView;
  private Menu menuMode;
  private Menu menuHelp;
  
  
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
  private MenuItem itemModCourse;
  private MenuItem itemModCat;
  private MenuItem itemModReq;
  private MenuItem itemModMS;
  private MenuItem itemModGroup;
  private MenuItem itemEditor;
  private MenuItem itemEvaluator;
  private MenuItem itemFilterShow;
  private MenuItem itemFilterClear;
  private MenuItem itemShowCatalogueStatistics;
  private MenuItem itemShowGroupStatistics;
  private MenuItem itemPresentation;
  private MenuItem itemImport;
  private MenuItem itemAbout;
  private MenuItem itemHelp;
  
  
  private MenuBar menuBar = new MenuBar();
  
  
  private ArrayList<String> editorItems = new ArrayList<>();
  private ArrayList<String> evaluatorItems = new ArrayList<>();
  
  private ArrayList<String> catNeeded = new ArrayList<>();
  private ArrayList<String> groupNeeded = new ArrayList<>();
  private ArrayList<String> openItems = new ArrayList<>();
  
  private HashMap<String, String> activeKeyBindings = new HashMap<>();
  
  private MenuHandler handler = null;
  
  private MenuManager() {
    registerComponents();
    assembleMenus();
    
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
  
  public void enableOpenItems() {
    setDisableForItems(openItems, false);
    
  }
  
  private void registerComponents() {
    registerMenuFileComponents();
    registerMenuEditComponents();
    registerMenuViewComponents();
    registerMenuModeComponents();
    registerMenuHelpComponents();
  }
  
  /**
   * Registers and instantiates components of menu 'help'
   */
  private void registerMenuHelpComponents() {
    // (Sub) Menu
    registerMenu(MENU_HELP, menuHelp = new Menu("Help"));
    // Items
    registerMenuItem(ITEM_ABOUT, itemAbout = new MenuItem("About..."));
    registerMenuItem(ITEM_HELP, itemHelp = new MenuItem("Help..."));
  }
  
  /**
   * Registers and instantiates components of menu 'mode'
   */
  private void registerMenuModeComponents() {
    // (Sub) Menus
    registerMenu(MENU_MODE, menuMode = new Menu("Mode"));
    // Items
    registerMenuItem(ITEM_EDITOR, itemEditor = new MenuItem("Editor"));
    registerMenuItem(ITEM_EVALUATOR, itemEvaluator = new MenuItem("Evaluator"));
  }
  
  /**
   * Registers and instantiates components of menu 'view'
   */
  private void registerMenuViewComponents() {
    // (Sub) Menus
    registerMenu(MENU_VIEW, menuView = new Menu("View"));
    // Items
    registerMenuItem(ITEM_SHOW_FILTERBAR, itemFilterShow = new MenuItem("Show Filter Bar"));
    registerMenuItem(ITEM_CLEAR_FILTERS, itemFilterClear = new MenuItem("Clear Filter"));
    registerMenuItem(ITEM_SHOW_CATALOGUE_STATISTICS, itemShowCatalogueStatistics = new MenuItem("Catalogue Overview..."));
    registerMenuItem(ITEM_SHOW_GROUP_STATISTICS, itemShowGroupStatistics = new MenuItem("Groups Overview..."));
    registerMenuItem(ITEM_PRESENTATION_MODE, itemPresentation = new RadioMenuItem("Presentation Mode"));
    // Categories
    groupNeeded.add(ITEM_SHOW_GROUP_STATISTICS);
    catNeeded.addAll(Arrays.asList(ITEM_CLEAR_FILTERS, ITEM_SHOW_FILTERBAR, ITEM_SHOW_CATALOGUE_STATISTICS));
  }
  
  /**
   * Registers and instantiates components of menu 'edit'
   */
  private void registerMenuEditComponents() {
    // (Sub) Menus
    registerMenu(MENU_EDIT, menuEdit = new Menu("Edit"));
    registerMenu(MENU_MODIFY, menuModify = new Menu("Modify"));
    // Items
    // MODIFY
    registerEditorItem(ITEM_MOD_COURSE, itemModCourse = new MenuItem("Modify Course..."), true);
    registerEditorItem(ITEM_MOD_CAT, itemModCat = new MenuItem("Modify Catalogue..."), true);
    registerEvaluatorItem(ITEM_MOD_GROUP, itemModGroup = new MenuItem("Modify Group"), true);
    registerEditorItem(ITEM_MOD_REQ, itemModReq = new MenuItem("Modify Requirement..."), true);
    registerEditorItem(ITEM_MOD_MS, itemModMS = new MenuItem("Modify Milestone..."), true);
    // Remaining
    registerEditorItem(ITEM_NEW_REQ, itemNewReq = new MenuItem("New Requirement..."), true);
    registerEditorItem(ITEM_NEW_MS, itemNewMS = new MenuItem("New Milestone..."), true);
    registerEvaluatorItem(ITEM_SPLIT_GROUP, itemSplitGroup = new MenuItem("Split Group..."), true);
  }
  
  /**
   * Registers and instantiates components of menu 'file'
   */
  private void registerMenuFileComponents() {
    // (Sub) Menus
    registerMenu(MENU_FILE, menuFile = new Menu("File"));
    registerMenu(MENU_NEW, menuNew = new Menu("New"));
    registerMenu(MENU_OPEN, menuOpen = new Menu("Open"));
    registerMenu(MENU_SAVE, menuSave = new Menu("Save"));
    registerMenu(MENU_EXPORT, menuExport = new Menu("Export"));
    // Items
    // NEW
    registerMenuItem(ITEM_NEW_COURSE, itemNewCourse = new MenuItem("New Course..."));
    registerMenuItem(ITEM_NEW_CAT, itemNewCat = new MenuItem("New Catalogue..."));
    registerMenuItem(ITEM_NEW_GROUP, itemNewGroup = new MenuItem("New Group..."));
    // OPEN
    registerMenuItem(ITEM_OPEN_COURSE, itemOpenCourse = new MenuItem("Open Course..."));
    registerMenuItem(ITEM_OPEN_CAT, itemOpenCat = new MenuItem("Open Catalogue..."));
    registerMenuItem(ITEM_OPEN_GROUPS, itemOpenGroup = new MenuItem("Open Group..."));
    // SAVE
    registerEditorItem(ITEM_SAVE_COURSE, itemSaveCourse = new MenuItem("Save Course"));
    registerEditorItem(ITEM_SAVE_COURSE_AS, itemSaveCourseAs = new MenuItem("Save Course As..."));
    registerEditorItem(ITEM_SAVE_CAT, itemSaveCat = new MenuItem("Save Catalogue"), true);
    registerEditorItem(ITEM_SAVE_CAT_AS, itemSaveCatAs = new MenuItem("Save Catalogue As..."), true);
    registerEvaluatorItem(ITEM_SAVE_GROUP, itemSaveGroup = new MenuItem("Save Group"), true);
    registerEvaluatorItem(ITEM_SAVE_GROUP_AS, itemSaveGroupAs = new MenuItem("Save Group As..."), true);
    // EXPORT
    registerEditorItem(ITEM_EXPORT_CAT, itemExportCat = new MenuItem("Export Catalogue..."), true);
    registerEvaluatorItem(ITEM_EXPORT_GROUP, itemExportGroup = new MenuItem("Export Active Group..."), true);
    registerEvaluatorItem(ITEM_EXPORT_GROUPS, itemExportGroups = new MenuItem("Export Groups..."), true);
    // Remaining Items
    registerMenuItem(ITEM_IMPORT, itemImport = new MenuItem("Import Catalogue..."));
    registerMenuItem(ITEM_QUIT, itemQuit = new MenuItem("Quit"));
    // Categories:
    catNeeded.add(ITEM_NEW_GROUP);
    openItems.add(ITEM_OPEN_COURSE);
    openItems.add(ITEM_OPEN_CAT);
    openItems.add(ITEM_IMPORT);
    openItems.add(ITEM_OPEN_GROUPS);
    catNeeded.add(ITEM_OPEN_GROUPS);
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
    activeKeyBindings.put(ITEM_SHOW_FILTERBAR, "Ctrl+F");
    activeKeyBindings.put(ITEM_SHOW_GROUP_STATISTICS, "Ctrl+Shift+O");
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
            case ITEM_EDITOR:
              handler.handleShowEditor(event);
              break;
            case ITEM_EVALUATOR:
              handler.handleShowEvaluator(event);
              break;
            case ITEM_PRESENTATION_MODE:
              handler.handlePresentationMode(event);
              break;
            case ITEM_CLEAR_FILTERS:
              handler.handleClearFilter(event);
              break;
            case ITEM_SHOW_FILTERBAR:
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
            case ITEM_ABOUT:
              handler.handleShowAbout(event);
              break;
            case ITEM_HELP:
              handler.handleShowHelp(event);
              break;
            case ITEM_MOD_COURSE:
              handler.handleModCourse(event);
              break;
            default:
              LOGGER.warn("Unknown menu key: {}, ignoring" + key);
          }
        }
      }
    }
  }
  
  /**
   * Assembles the menu 'file' with its submenues.
   * Expects all objects being created beforehand
   */
  private void assembleMenuFile() {
    // Submenus:
    menuNew.getItems().addAll(
        itemNewCourse,
        itemNewCat,
        itemNewGroup);
    menuOpen.getItems().addAll(
        itemOpenCourse,
        itemOpenCat,
        itemOpenGroup);
    menuSave.getItems().addAll(
        itemSaveCourse,
        itemSaveCourseAs,
        itemSaveCat,
        itemSaveCatAs,
        itemSaveGroup,
        itemSaveGroupAs);
    menuExport.getItems().addAll(
        itemExportCat,
        itemExportGroup,
        itemExportGroups);
    // The menu:
    menuFile.getItems().addAll(
        menuNew,
        menuOpen,
        menuSave,
        new SeparatorMenuItem(),
        menuExport,
        new SeparatorMenuItem(),
        itemImport,
        new SeparatorMenuItem(),
        itemQuit);
  }
  
  /**
   * Assembles the menu 'edit' with its submenu.
   * Expects all objects being created beforehand
   */
  private void assembleMenuEdit() {
    // Submenu
    menuModify.getItems().addAll(
        itemModCourse,
        itemModCat,
        itemModGroup,
        itemModReq,
        itemModMS);
    // The Menu:
    menuEdit.getItems().addAll(
        itemNewReq,
        itemNewMS,
        new SeparatorMenuItem(),
        menuModify,
        new SeparatorMenuItem(),
        itemSplitGroup);
  }
  
  /**
   * Assembles the menu 'view' with its submenu.
   * Expects all objects being created beforehand
   */
  private void assembleMenuView() {
    // The Menu:
    menuView.getItems().addAll(
        itemFilterShow,
        itemFilterClear,
        new SeparatorMenuItem(),
        itemShowCatalogueStatistics,
        itemShowGroupStatistics,
        new SeparatorMenuItem(),
        itemPresentation);
  }
  
  /**
   * Assembles the menu 'mode' with its submenu.
   * Expects all objects being created beforehand
   */
  private void assembleMenuMode() {
    // The Menu:
    menuMode.getItems().addAll(
        itemEditor,
        itemEvaluator);
  }
  
  private void assembleMenus() {
    assembleMenuFile();
    assembleMenuEdit();
    assembleMenuView();
    assembleMenuMode();
    assemleMenuHelp();
    
    menuBar.getMenus().addAll(menuFile, menuEdit, menuView, menuMode, menuHelp);
  }
  
  private void assemleMenuHelp() {
    // The Menu:
    menuHelp.getItems().addAll(
        itemAbout,
        itemHelp
    );
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
