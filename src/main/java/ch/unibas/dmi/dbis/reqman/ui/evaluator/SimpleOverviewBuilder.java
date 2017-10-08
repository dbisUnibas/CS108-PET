package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Milestone;
import j2html.tags.Tag;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.DoubleStream;

import static j2html.TagCreator.*;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class SimpleOverviewBuilder {

    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(SimpleOverviewBuilder.class);
    private ArrayList<Group> groups;
    private Catalogue catalogue;
    private Map<Double, Group> totalMapping = new HashMap<>();
    private Map<Integer, Map<Double, Group>> milestoneMapping = new HashMap<>();

    public SimpleOverviewBuilder(Catalogue catalogue, List<Group> groups) {
        this.catalogue = catalogue;
        this.groups = new ArrayList<>(groups);
        initMilestoneMapping();
        initTotalMapping();
        LOGGER.debug("totalMappings: " + totalMapping.toString());
        LOGGER.debug("milestoneMappings: " + milestoneMapping.toString());
    }

    String exportOverviewHTML() {
        String html = html().with(
                overviewHeader(),
                body().with(
                        heading(),
                        totalSumTable(),
                        statisticsTable()
                )
        ).render();
        return prependDoctype(html);
    }

    private DoubleStream points() {
        ArrayList<Double> points = new ArrayList<>();
        groups.forEach(g -> points.add(g.getTotalSum(catalogue)));
        return points.stream().mapToDouble(Double::doubleValue);
    }

    private DoubleStream pointsPerMS(Milestone ms) {
        ArrayList<Double> points = new ArrayList<>();
        groups.forEach(g -> points.add(g.getSumForMilestone(ms, catalogue)));
        return points.stream().mapToDouble(Double::doubleValue);
    }

    private Tag totalSumHeaderRow() {
        ArrayList<Tag> cells = new ArrayList<>();
        groups.forEach(group -> {
            cells.add(th(group.getName()));
        });
        cells.add(0, th());
        return tr().with(cells.toArray(new Tag[0]));
    }

    private Tag totalSumMSRow(Milestone ms) {
        ArrayList<Tag> cells = new ArrayList<>();
        groups.forEach(group -> {
            cells.add(td(StringUtils.roundTo2Digits(group.getSumForMilestone(ms, catalogue))));
        });
        cells.add(0, td().with(b(ms.getName())));
        return tr().with(cells.toArray(new Tag[0]));
    }

    private Tag totalSumTable() {
        ArrayList<Tag> rows = new ArrayList<>();
        rows.add(totalSumHeaderRow());
        catalogue.getMilestones().forEach(ms -> {
            rows.add(totalSumMSRow(ms));
        });
        rows.add(totalSumRow());
        return div().with(h2("Overall Points"),
                table().with(rows.toArray(new Tag[0]))
        );
    }

    private Tag overviewHeader() {
        return head().with(
                title("Overview of " + catalogue.getLecture()),
                style().withText("body {font-family: sans-serif;padding:20px;}" +
                        "table, th, td {border: 1px solid black;padding: 5px;}" +
                        "table {border-collapse: collapse}")
        );
    }

    private Tag heading() {
        return h1("Overview of groups attending to " + catalogue.getLecture());
    }

    private Tag totalSumRow() {
        ArrayList<Tag> cells = new ArrayList<>();
        groups.forEach(group -> {
            cells.add(td(StringUtils.roundTo2Digits(group.getTotalSum(catalogue))));
        });
        cells.add(0, td().with(b("Total:")));

        return tr().with(cells.toArray(new Tag[0]));
    }

    private String prependDoctype(String html) {
        return "<!DOCTYPE html>" + html;
    }

    private Tag statisticsRowMS(Milestone ms) {
        ArrayList<Tag> cells = new ArrayList<>();
        // Milestone name
        cells.add(td(ms.getName()));
        // min
        OptionalDouble value = pointsPerMS(ms).min();
        Group g = milestoneMapping.get(ms.getOrdinal()).get(value.orElse(-1));
        String groupName = g != null ? g.getName() : "null";
        cells.add(td(StringUtils.roundTo2Digits(value.orElse(-1)) + String.format(" [%s]", groupName)));
        // avg
        value = pointsPerMS(ms).average();
        cells.add(td(StringUtils.roundTo2Digits(value.orElse(-1))));
        // max
        value = pointsPerMS(ms).max();
        g = milestoneMapping.get(ms.getOrdinal()).get(value.orElse(-1));
        groupName = g != null ? g.getName() : "null";

        cells.add(td(StringUtils.roundTo2Digits(value.orElse(-1)) + String.format(" [%s]", groupName)));
        return tr().with(cells.toArray(new Tag[0]));
    }

    private Tag statisticsTotal() {
        ArrayList<Tag> cells = new ArrayList<>();
        cells.add(td().with(b("Total")));
        // min
        OptionalDouble value = points().min();
        Group g = totalMapping.get(value.orElse(-1));
        String name = g != null ? g.getName() : "null";
        cells.add(td(StringUtils.roundTo2Digits(value.orElse(-1)) + String.format(" [%s]", name)));
        // avg
        value = points().average();
        cells.add(td(StringUtils.roundTo2Digits(value.orElse(-1))));
        // max
        value = points().max();
        g = totalMapping.get(value.orElse(-1));
        name = g != null ? g.getName() : "null";

        cells.add(td(StringUtils.roundTo2Digits(value.orElse(-1)) + String.format(" [%s]", name)));

        return tr().with(cells.toArray(new Tag[0]));
    }

    private Tag statisticsTable() {
        ArrayList<Tag> rows = new ArrayList<>();
        rows.add(statisticsHeader());
        catalogue.getMilestones().forEach(ms -> {
            rows.add(statisticsRowMS(ms));
        });
        rows.add(statisticsTotal());
        return div().with(h2("Statistics"),
                table().with(rows.toArray(new Tag[0])),
                p().withStyle("font-size: 0.8em").with(i("Brackets requested by Sein"))
        );
    }

    private Tag statisticsHeader() {
        return tr().with(
                th(),
                th("Min [Group Name]"),
                th("Average"),
                th("Max [Group Name]")
        );
    }

    private void initTotalMapping() {
        groups.forEach(g -> totalMapping.put(g.getTotalSum(catalogue), g));
    }

    private void initMilestoneMapping() {
        catalogue.getMilestones().forEach(ms -> {
            HashMap<Double, Group> map = new HashMap<>();
            groups.forEach(g -> {
                map.put(g.getSumForMilestone(ms, catalogue), g);
            });
            milestoneMapping.put(ms.getOrdinal(), map);
        });
    }
}
