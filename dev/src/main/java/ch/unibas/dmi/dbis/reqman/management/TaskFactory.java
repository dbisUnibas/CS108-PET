package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;

import java.io.File;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class TaskFactory {

    private TaskFactory(){
        // Factory class - no constructor needed
    }

    public CheckedAsynchronousOperation<Catalogue> createLoadCatalogueOperation(File catFile){
        OpenCatalogueTask task = new OpenCatalogueTask(catFile);
        CheckedAsynchronousOperation<Catalogue> operation = new CheckedAsynchronousOperation<>(task);



        return operation;
    }
}
