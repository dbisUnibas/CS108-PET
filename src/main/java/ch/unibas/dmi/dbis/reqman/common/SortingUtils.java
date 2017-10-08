package ch.unibas.dmi.dbis.reqman.common;

import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Progress;
import ch.unibas.dmi.dbis.reqman.data.Requirement;

import java.util.Comparator;

/**
 * A class which contains several {@link Comparator}s which are used across the software.
 * <p>
 * Since instances of this class are not needed, there is no public constructor for it.
 *
 * @author loris.sauter
 */
public class SortingUtils {

    /**
     * A {@link Comparator} for {@link Boolean}s which sorts booleans with value {@code true} first.
     */
    public static final Comparator<Boolean> TRUE_FIRST_COMPARATOR = (b1, b2) -> {
        if (b1 == b2) {
            return 0;
        } else if (b1) {
            return -1;
        } else {
            return 1;
        }
    };
    /**
     * A {@link Comparator} for {@link Boolean}s which sorts booleans with value {@code false} first.
     * This is exactly the {@link Comparator} resulting by invoking {@code {@link SortingUtils#FALSE_FIRST_COMPARATOR}.reversed()}
     */
    public static final Comparator<Boolean> FALSE_FIRST_COMPARATOR = TRUE_FIRST_COMPARATOR.reversed();
    /**
     * The {@link Comparator} used to compare two requirements.
     * The sorting order is based on:
     * <ol>
     * <li>the requirement's associated minimal milestone</li>
     * <li>if the requirement is mandatory (mandatory ones first)</li>
     * <li>if the requirement is regular (malus requirements last)</li>
     * <lI>the requirement's name, in lexicographic order</lI>
     * </ol>
     */
    public static final Comparator<Requirement> REQUIREMENT_COMPARATOR = Comparator.comparingInt(Requirement::getMinMilestoneOrdinal)
            .thenComparing(Requirement::isMandatory, TRUE_FIRST_COMPARATOR)
            .thenComparing(Requirement::isMalus, FALSE_FIRST_COMPARATOR)
            .thenComparing(Requirement::getName);

    /**
     * Utility class, no instance needed.
     */
    private SortingUtils() {
        // No constructor needed
    }

    /**
     * The {@link Comparator} used to compare {@link Progress} objects.
     *
     * The sorting of progress objects mirrors the sorting of the corresponding {@link Requirement} objects.
     *
     * @param cat The catalogue to which the progress refers
     * @see SortingUtils#REQUIREMENT_COMPARATOR
     */
    public static final Comparator<Progress> getProgressComparator(Catalogue cat) {
        return Comparator.comparing(cat::getRequirementForProgress, REQUIREMENT_COMPARATOR);
    }
}
