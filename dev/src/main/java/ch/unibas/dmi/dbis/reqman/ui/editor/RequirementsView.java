package ch.unibas.dmi.dbis.reqman.ui.editor;

import ch.unibas.dmi.dbis.reqman.core.Requirement;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListController;
import ch.unibas.dmi.dbis.reqman.ui.common.ModifiableListView;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class RequirementsView extends ModifiableListView<Requirement> {

    public RequirementsView(String title, ModifiableListController<Requirement> controller) {
        super("Requirement", controller);
    }
}
