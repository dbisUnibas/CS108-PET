package ch.unibas.dmi.dbis.reqman.core;

import java.util.*;

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

    public Requirement() {

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

    public void addPredecessorName(String name) {
        predecessorNames.add(name);
    }

    public boolean removePredecessorName(String name) {
        return predecessorNames.remove(name);
    }

    public List<String> getPredecessorNames() {
        return new ArrayList<String>(predecessorNames);
    }


    public String addProperty(String key, String value) {
        return propertiesMap.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Requirement that = (Requirement) o;

        if (Double.compare(that.getMaxPoints(), getMaxPoints()) != 0) {
            return false;
        }
        if (isBinary() != that.isBinary()) {
            return false;
        }
        if (isMandatory() != that.isMandatory()) {
            return false;
        }
        if (isMalus() != that.isMalus()) {
            return false;
        }
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) {
            return false;
        }
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null) {
            return false;
        }
        if (getMinMilestoneName() != null ? !getMinMilestoneName().equals(that.getMinMilestoneName()) : that.getMinMilestoneName() != null) {
            return false;
        }
        if (getMaxMilestoneName() != null ? !getMaxMilestoneName().equals(that.getMaxMilestoneName()) : that.getMaxMilestoneName() != null) {
            return false;
        }
        if (getPredecessorNames() != null ? !getPredecessorNames().equals(that.getPredecessorNames()) : that.getPredecessorNames() != null) {
            return false;
        }
        return getPropertiesMap() != null ? getPropertiesMap().equals(that.getPropertiesMap()) : that.getPropertiesMap() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getMinMilestoneName() != null ? getMinMilestoneName().hashCode() : 0);
        result = 31 * result + (getMaxMilestoneName() != null ? getMaxMilestoneName().hashCode() : 0);
        temp = Double.doubleToLongBits(getMaxPoints());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (isBinary() ? 1 : 0);
        result = 31 * result + (isMandatory() ? 1 : 0);
        result = 31 * result + (getPredecessorNames() != null ? getPredecessorNames().hashCode() : 0);
        result = 31 * result + (getPropertiesMap() != null ? getPropertiesMap().hashCode() : 0);
        result = 31 * result + (isMalus() ? 1 : 0);
        return result;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMinMilestoneName() {
        return minMilestoneName;
    }

    public void setMinMilestoneName(String minMilestoneName) {
        this.minMilestoneName = minMilestoneName;
    }

    public String getMaxMilestoneName() {
        return maxMilestoneName;
    }

    public void setMaxMilestoneName(String maxMilestoneName) {
        this.maxMilestoneName = maxMilestoneName;
    }

    public double getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(double maxPoints) {
        this.maxPoints = maxPoints;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isMalus() {
        return malus;
    }

    public void setMalus(boolean malus) {
        this.malus = malus;
    }

    public String removeProperty(String key) {
        return propertiesMap.remove(key);

    }

    public Map<String, String> getPropertiesMap() {
        return new HashMap<String, String>(propertiesMap);
    }
}
