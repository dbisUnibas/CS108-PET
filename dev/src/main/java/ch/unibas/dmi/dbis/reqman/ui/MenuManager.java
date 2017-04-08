package ch.unibas.dmi.dbis.reqman.ui;


import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class MenuManager {

    private static MenuManager instance = null;
    private HashMap<String, MenuItem> menuItems = new HashMap<>();
    private HashMap<String, Menu> menus = new HashMap<>();
    private Menu menuFile;
    private Menu menuEdit;
    private Menu menuEvaluate;
    private Menu menuView;
    private Menu menuHelp;
    private MenuItem itemNewCat;
    private MenuItem itemNewGroup;
    private MenuItem itemOpenCat;
    private MenuItem itemOpenGroup;
    private MenuItem itemSaveCat;
    private MenuItem itemSaveGroup;
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
    private MenuItem itemEditor;
    private MenuItem itemEvaluator;
    private MenuBar menuBar = new MenuBar();
    private ArrayList<String> editorItems = new ArrayList<>();
    private ArrayList<String> evaluatorItems = new ArrayList<>();

    private ArrayList<String> catNeeded = new ArrayList<>();
    private ArrayList<String> groupNeeded = new ArrayList<>();

    private MenuManager() {
        registerMenu("menuFile", menuFile = new Menu("File"));
        registerMenu("menuEdit", menuEdit = new Menu("Edit"));
        registerMenu("menuEvaluate", menuEvaluate = new Menu("Evaluate"));
        registerMenu("menuView", menuView = new Menu("View"));
        registerMenu("menuHelp", menuHelp = new Menu("Help"));

        registerMenuItem("itemNewCat", itemNewCat = new MenuItem("New Catalogue..."));
        registerMenuItem("itemNewGroup", itemNewGroup = new MenuItem("New Group..."));
        registerMenuItem("itemOpenCat", itemOpenCat = new MenuItem("Open Catalogue..."));
        registerMenuItem("itemOpenGroup", itemOpenGroup = new MenuItem("Open Group..."));

        registerEditorItem("itemSaveCat", itemSaveCat = new MenuItem("Save Catalogue"), true);
        registerEvaluatorItem(" itemSaveGroup", itemSaveGroup = new MenuItem("Save Group"), true);
        registerEditorItem("itemSaveCatAs", itemSaveCatAs = new MenuItem("Save Catalogue As..."), true);
        registerEvaluatorItem("itemSaveGroupAs", itemSaveGroupAs = new MenuItem("Save Group As..."), true);

        registerEditorItem("itemExportCat", itemExportCat = new MenuItem("Export Catalogue..."), true);
        registerEvaluatorItem("itemExportGroups", itemExportGroups = new MenuItem("Export Groups..."), true);
        registerEvaluatorItem("itemExportGroup", itemExportGroup = new MenuItem("Export Group..."), true);

        registerMenuItem("itemQuit", itemQuit = new MenuItem("Quit"));

        registerEditorItem("itemNewReq", itemNewReq = new MenuItem("New Requirement..."), true);
        registerEditorItem("itemNewMS", itemNewMS = new MenuItem("New Milestone..."), true);
        registerEditorItem("itemModCat", itemModCat = new MenuItem("Modify Catalogue..."), true);
        registerEditorItem("itemModReq", itemModReq = new MenuItem("Modify Requirement..."), true);
        registerEditorItem("itemModMS", itemModMS = new MenuItem("Modify Milestone..."), true);
        registerEvaluatorItem("itemModGroup", itemModGroup = new MenuItem("Modify Group"), true);

        registerEvaluatorItem("itemShowOverview", itemShowOverview = new MenuItem("Show Overview"), true);

        registerMenuItem("itemEditor", itemEditor = new MenuItem("Editor"));
        registerMenuItem("itemEvaluator", itemEvaluator = new MenuItem("Evaluator"));

        assembleMenus();
        menuBar.getMenus().addAll(menuFile, menuEdit, menuEvaluate, menuView, menuHelp);

        // TEMP
        menuHelp.setDisable(true);
    }

    public static MenuManager getInstance() {
        if (instance == null) {
            instance = new MenuManager();
        }
        return instance;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    private void assembleMenus() {
        menuFile.getItems().addAll(itemNewCat,
                itemNewGroup,
                new SeparatorMenuItem(),
                itemOpenCat, itemOpenGroup,
                new SeparatorMenuItem(),
                itemSaveCat, itemSaveCatAs, itemSaveGroup, itemSaveGroupAs,
                new SeparatorMenuItem(),
                itemExportCat, itemExportGroups, itemExportGroup,
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
        menuEvaluate.getItems().addAll(itemShowOverview);

        menuView.getItems().addAll(itemEditor, itemEvaluator);
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
        keyProvider.forEach(key -> menuItems.get(key).setDisable(disable));
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
        setDisableForItems(groupNeeded, false);
    }

    public void disableAllButInitial() {
        disableCatalogueNeeded();
        disableEditorItems();
        disableGroupNeeded();
        disableEvaluatorItems();
    }

}
