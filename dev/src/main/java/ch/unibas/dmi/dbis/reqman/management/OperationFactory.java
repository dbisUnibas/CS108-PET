package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;

import java.io.File;
import java.util.function.Consumer;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class OperationFactory {

    private OperationFactory(){
        // Factory class - no constructor needed
    }

    public static CheckedAsynchronousOperation<Catalogue> createLoadCatalogueOperation(File catFile, Consumer<Catalogue> processor){
        OpenCatalogueTask task = new OpenCatalogueTask(catFile);
        CheckedAsynchronousOperation<Catalogue> operation = new CheckedAsynchronousOperation<>(task);
        operation.addProcessor(processor);
        return operation;
    }
}
