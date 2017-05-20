package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.ui.StatusBar;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class OperationFactory {

    private static StatusBar statusBar = null;

    private OperationFactory() {
        // Factory class - no constructor needed
    }

    public static void registerStatusBar(StatusBar bar) {
        statusBar = bar;
    }

    public static CheckedAsynchronousOperation<Catalogue> createLoadCatalogueOperation(File catFile, Consumer<Catalogue> processor) {
        OpenCatalogueTask task = new OpenCatalogueTask(catFile);
        CheckedAsynchronousOperation<Catalogue> operation = createOperationForTask(task, true);
        operation.addProcessor(processor);
        return operation;
    }

    public static CheckedAsynchronousOperation<Boolean> createSaveCatalogueOperation(Catalogue catalogue, File catFile) {
        SaveCatalogueTask task = new SaveCatalogueTask(catalogue, catFile);
        return createOperationForTask(task, true);
    }

    public static CheckedAsynchronousOperation<Boolean> createExportCatalogueOperation(Catalogue cat, File file) {
        ExportCatalogueTask task = new ExportCatalogueTask(cat, file);
        return createOperationForTask(task, true);
    }

    public static CheckedAsynchronousOperation<Group> createOpenGroupOperation(File file) {
        OpenGroupTask task = new OpenGroupTask(file);
        return createOperationForTask(task, true);
    }

    public static CheckedAsynchronousOperation<Boolean> createSaveGroupOperation(File file, Group group) {
        SaveGroupTask task = new SaveGroupTask(file, group);
        return createOperationForTask(task, true);
    }

    public static CheckedAsynchronousOperation<Boolean> createExportMultipleGroupsOperation(File dir, List<Group> groups, Catalogue catalogue) {
        ExportMultipleGroupTask task = new ExportMultipleGroupTask(dir, groups, catalogue);
        return createOperationForTask(task, true);
    }

    public static CheckedAsynchronousOperation<Boolean> createSaveAsBackupOperation(Group group, File catFile) {
        SaveGroupBackupTask task = new SaveGroupBackupTask(group, catFile);
        return createOperationForTask(task, false); // deamon=false ensures finish execution before closing application
    }

    public static CheckedAsynchronousOperation<List<OpenBackupsTask.BackupObject>> createOpenBackupsOperation() {
        OpenBackupsTask task = new OpenBackupsTask();
        return createOperationForTask(task, true);
    }

    public static CheckedAsynchronousOperation<List<Group>> createOpenMultipleGroupOperation(List<File> files) {
        OpenMultipleGroupsTask task = new OpenMultipleGroupsTask(files);
        return createOperationForTask(task, true);
    }

    public static CheckedAsynchronousOperation<Boolean> createExportOverviewOperation(OverviewSnapshot snapshot, File exportFile) {
        ExportOverviewTask task = new ExportOverviewTask(snapshot, exportFile);
        return createOperationForTask(task, true);
    }

    private static <T> CheckedAsynchronousOperation<T> createOperationForTask(ManagementTask<T> task, boolean deamon) {
        CheckedAsynchronousOperation<T> op = new CheckedAsynchronousOperation<T>(task, deamon);
        bindStatusBar(op);
        return op;
    }

    private static <T> void bindStatusBar(CheckedAsynchronousOperation<T> operation) {
        if (statusBar != null) {
            operation.setStatusBar(statusBar);
        }
    }
}
