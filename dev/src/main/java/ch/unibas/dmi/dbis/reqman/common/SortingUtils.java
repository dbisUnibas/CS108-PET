package ch.unibas.dmi.dbis.reqman.common;

import ch.unibas.dmi.dbis.reqman.core.Requirement;

import java.util.Comparator;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class SortingUtils {

    private SortingUtils(){
        // No constructor needed
    }

    public static final Comparator<Boolean> TRUE_FIRST_COMPARATOR = (b1, b2) -> {
        if (b1 == b2) {
            return 0;
        } else if (b1) {
            return -1;
        } else {
            return 1;
        }
    };

    public static final Comparator<Boolean> FALSE_FIRST_COMPARATOR = TRUE_FIRST_COMPARATOR.reversed();

    public static final Comparator<Requirement> REQUIREMENT_COMPARATOR = Comparator.comparingInt(Requirement::getMinMilestoneOrdinal)
            .thenComparing(Requirement::isMandatory, TRUE_FIRST_COMPARATOR)

            .thenComparing(Requirement::isMalus, FALSE_FIRST_COMPARATOR).thenComparing(Requirement::getName);
}
