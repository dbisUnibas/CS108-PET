package ch.unibas.dmi.dbis.reqman.management;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Group;
import ch.unibas.dmi.dbis.reqman.core.Milestone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class OverviewSnapshot {

    private final Catalogue catalogue;
    private final Group[] groups;

    private Map<Integer, HashMap<Double, List<Group>>> msMap;
    private Map<Double, List<Group>> totalMap;


    public OverviewSnapshot(Catalogue catalogue, Group... groups) {
        this.catalogue = catalogue;
        this.groups = groups;

        initTotalMap();
        initMsMap();
    }

    public Group getGroup(String parameter) {
        for (Group g : groups) {
            if (g.getName().equals(parameter)) {
                return g;
            }
        }
        return null;
    }

    public String getMilestoneName(int ordinal) {
        return catalogue.getMilestoneByOrdinal(ordinal).getName();
    }

    public String getCatalogueName() {
        return catalogue.getName();
    }

    public double getTotalMin() {
        return totalPointsStream().min().orElse(-1);
    }

    public List<Group> getTotalMinList() {
        return totalMap.get(getTotalMin());
    }

    public double getTotalAvg() {
        return totalPointsStream().average().orElse(-1);
    }

    public List<Group> getTotalAvgList() {
        return totalMap.get(getTotalAvg());
    }

    public double getTotalMax() {
        return totalPointsStream().max().orElse(-1);
    }

    public List<Group> getTotalMaxList() {
        return totalMap.get(getTotalMax());
    }

    public double getMsMin(int ordinal) {
        return perMsPointsStream(ordinal).min().orElse(-1);
    }

    public List<Group> getMsMinList(int ordinal) {
        return msMap.get(ordinal).get(getMsMin(ordinal));
    }

    public double getMsAvg(int ordinal) {
        return perMsPointsStream(ordinal).average().orElse(-1);
    }

    public List<Group> getMsAvgList(int ordinal) {
        return msMap.get(ordinal).get(getMsAvg(ordinal));
    }

    public double getMsMax(int ordinal) {
        return perMsPointsStream(ordinal).max().orElse(-1);
    }

    public List<Group> getMsMaxList(int ordinal) {
        return msMap.get(ordinal).get(getMsMax(ordinal));
    }

    public double getTotalSum(Group g) {
        return g.getTotalSum(catalogue);
    }

    public double getMsSum(Group g, int ms) {
        return g.getSumForMilestone(catalogue.getMilestoneByOrdinal(ms), catalogue);
    }

    private DoubleStream totalPointsStream() {
        return pointsStream(-1);
    }

    /**
     * MS == -1 -> total
     *
     * @param ms
     * @return
     */
    private DoubleStream pointsStream(int ms) {
        ArrayList<Double> points = new ArrayList<>();
        for (Group g : groups) {
            if (ms == -1) {
                points.add(g.getTotalSum(catalogue));
            } else {
                points.add(g.getSumForMilestone(catalogue.getMilestoneByOrdinal(ms), catalogue));
            }
        }
        return points.stream().mapToDouble(Double::valueOf);
    }

    private DoubleStream perMsPointsStream(int ms) {
        if (ms <= 0) {
            throw new IllegalArgumentException("Milestone ordinals are 1 based positive integers");
        }
        return pointsStream(ms);
    }

    private void initTotalMap() {
        totalMap = new HashMap<>();
        for (Group g : groups) {
            double sum = g.getTotalSum(catalogue);
            addToListMap(totalMap, sum, g);
        }
    }

    private void addToListMap(Map<Double, List<Group>> map, double key, Group g) {
        if (map.containsKey(key)) {
            map.get(key).add(g);
        } else {
            ArrayList<Group> list = new ArrayList<>();
            list.add(g);
            map.put(key, list);
        }
    }

    private void initMsMap() {
        msMap = new HashMap<>();
        for (Milestone ms : catalogue.getMilestones()) {
            HashMap<Double, List<Group>> map = new HashMap<>();

            // Foreach milestone:
            for (Group g : groups) {
                // Foreach group: check if msSum-grouplist already exists?
                double sum = g.getSumForMilestone(ms, catalogue);
                addToListMap(map, sum, g);
            }

            msMap.put(ms.getOrdinal(), map);
        }
    }


}