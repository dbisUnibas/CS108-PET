package ch.unibas.dmi.dbis.reqman.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * The class {@link Requirement} represents a requirement as defined by the definitions document.
 *
 * @author loris.sauter
 * @version 0.0.1
 */
public class Requirement {

    private String name;
    private String description;
    private String minMilestoneName;
    private String maxMilestoneName;
    private double maxPoints;
    private boolean binary;
    private boolean mandatory;
    private List<String> predecessorNames;
    private Map<String, String> propertiesMap;
    private boolean malus;

    public Requirement(){

    }

    public Requirement(String name, String description, String minMilestoneName, String maxMilestoneName, double maxPoints, boolean binary, boolean mandatory, boolean malus) {
        this.name = name;
        this.description = description;
        this.minMilestoneName = minMilestoneName;
        this.maxMilestoneName = maxMilestoneName;
        this.maxPoints = maxPoints;
        this.binary = binary;
        this.mandatory = mandatory;
        this.malus = malus;

        predecessorNames = new Vector<String>();
        propertiesMap = new HashMap<String, String>();
    }

    public void addPredecessorName(String name){
        predecessorNames.add(name);
    }
}
