package ch.unibas.dmi.dbis.reqman.ui.evaluator;

import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import j2html.tags.Tag;

import java.util.*;
import java.util.stream.DoubleStream;

import static j2html.TagCreator.*;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class SimpleOverviewBuilder {

    private ArrayList<Group> groups;

    private Catalogue catalogue;

    public SimpleOverviewBuilder(Catalogue catalogue, List<Group> groups){
        this.catalogue = catalogue;
        this.groups = new ArrayList<>(groups);
        initMilestoneMapping();
        initTotalMapping();
    }

    private DoubleStream points(){
        ArrayList<Double> points = new ArrayList<>();
        groups.forEach(g->points.add(g.getTotalSum(catalogue)));
        return points.stream().mapToDouble(Double::doubleValue);
    }

    private DoubleStream pointsPerMS(Milestone ms){
        ArrayList<Double> points = new ArrayList<>();
        groups.forEach(g-> points.add(g.getSumForMilestone(ms,catalogue)));
        return points.stream().mapToDouble(Double::doubleValue);
    }

    private Tag totalSumHeaderRow(){
        ArrayList<Tag> cells = new ArrayList<>();
        groups.forEach(group -> {
            cells.add(th(group.getName()));
        });
        cells.add(0, th() );
        return tr().with(cells.toArray(new Tag[0]));
    }

    private Tag totalSumMSRow(Milestone ms){
        ArrayList<Tag> cells = new ArrayList<>();
        groups.forEach(group -> {
            cells.add(td(StringUtils.prettyPrint(group.getSumForMilestone(ms,catalogue))));
        });
        cells.add(0, td().with(b(ms.getName())));
        return tr().with(cells.toArray(new Tag[0]));
    }

    private Tag totalSumTable(){
        ArrayList<Tag> rows = new ArrayList<>();
        rows.add(totalSumHeaderRow() );
        catalogue.getMilestones().forEach(ms -> {
            rows.add(totalSumMSRow(ms));
        });
        rows.add(totalSumRow() );
        return div().with(h2("Overall Points"),
                    table().with(rows.toArray(new Tag[0]) )
                );
    }

    private Tag overviewHeader(){
        return head().with(title("Overview of "+catalogue.getLecture()));
    }

    private Tag heading(){
        return h1("Overview of groups attending to "+catalogue.getLecture() );
    }

    private Tag totalSumRow(){
        ArrayList<Tag> cells = new ArrayList<>();
        groups.forEach(group -> {
           cells.add(td(StringUtils.prettyPrint(group.getTotalSum(catalogue))));
        });
        cells.add(0, td().with(b("Total:")));

        return tr().with(cells.toArray(new Tag[0]));
    }

    private String prependDoctype(String html) {
        return "<!DOCTYPE html>" + html;
    }

    private Tag statisticsRowMS(Milestone ms){
        ArrayList<Tag> cells = new ArrayList<>();
        // Milestone name
        cells.add(td(ms.getName()));
        // min
        OptionalDouble value = pointsPerMS(ms).min();
        cells.add(td(StringUtils.prettyPrint(value.orElse(-1))+String.format(" [%s]", milestoneMapping.get(ms.getOrdinal()).get(value.orElse(-1)))));
        // avg
        value = pointsPerMS(ms).average();
        cells.add(td(StringUtils.prettyPrint(value.orElse(-1))));
        // max
        value = pointsPerMS(ms).max();
        cells.add(td(StringUtils.prettyPrint(value.orElse(-1))+String.format(" [%s]", milestoneMapping.get(ms.getOrdinal()).get(value.orElse(-1)))));
        return tr().with(cells.toArray(new Tag[0]));
    }

    private Tag statisticsTotal(){
        ArrayList<Tag> cells = new ArrayList<>();
        // min
        OptionalDouble value = points().min();
        cells.add(td(StringUtils.prettyPrint(value.orElse(-1)) + totalMapping.get(value.orElse(-1))) );
        // avg
        value = points().average();
        cells.add(td(StringUtils.prettyPrint(value.orElse(-1)) + totalMapping.get(value.orElse(-1))));
        // max
        value = points().max();
        cells.add(td(StringUtils.prettyPrint(value.orElse(-1)) + totalMapping.get(value.orElse(-1))));

        return tr().with(cells.toArray(new Tag[0]));
    }

    private Tag statisticsTable(){
        ArrayList<Tag> rows = new ArrayList<>();
        rows.add(statisticsHeader() );
        catalogue.getMilestones().forEach(ms -> {
            rows.add(statisticsRowMS(ms));
        });
        rows.add(statisticsTotal() );
        return div().with(h2("Statistics"),
                table().with(rows.toArray(new Tag[0]) ),
                p().withStyle("font-size: 0.8em").with(i("Brackets requested by Sein"))
        );
    }

    private Tag statisticsHeader(){
        return tr().with(
                th(),
                th("Min [Group Name]"),
                th("Average"),
                th("Max [Group Name]")
            );
    }

    private Map<Double, Group> totalMapping = new HashMap<>();
    private Map<Integer, Map<Double, Group>> milestoneMapping = new HashMap<>();

    private void initTotalMapping(){
        groups.forEach(g -> totalMapping.put(g.getTotalSum(catalogue), g));
    }

    private void initMilestoneMapping(){
        catalogue.getMilestones().forEach(ms -> {
            groups.forEach(g -> {
                HashMap<Double, Group> map = new HashMap<Double, Group>();
                map.put(g.getSumForMilestone(ms, catalogue),g);
                milestoneMapping.put(ms.getOrdinal(), map);
            });
        });
    }

    public String exportOverviewHTML(){
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
}
