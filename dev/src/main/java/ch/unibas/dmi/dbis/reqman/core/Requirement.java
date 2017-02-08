package ch.unibas.dmi.dbis.reqman.core;

import java.util.*;

/**
 * The class {@link Requirement} represents a requirement as defined by the definitions document.
 *
 * @author loris.sauter
 */
public class Requirement {

    /**
     * The name of the requirement. Shall be unique.
     */
    private String name;
    /**
     * The desciption of the requirement. May be a long-ish string.
     */
    private String description;
    /**
     * The minimal milestone name this requirement firstly occurs
     */
    private String minMilestoneName;
    /**
     * The maximal milestone name this requirement must be met
     */
    private String maxMilestoneName;
    /**
     * The maximal amount of points received upon meeting this requirement
     */
    private double maxPoints;
    /**
     * Whether this requirement is binary or not:
     * If this requirement can be met or not, or if it
     * could potentially be partially met
     */
    private boolean binary;
    /**
     * Whether this requirement is mandatory or not.
     */
    private boolean mandatory;
    /**
     * A list of predecessor requirement names this requirement depends on.
     */
    private List<String> predecessorNames = new Vector<String>();
    /**
     * A map of key-value-pairs related to export this requirement
     */
    private Map<String, String> propertiesMap = new HashMap<String, String>();
    /**
     * Whether this requirement has a malus role or not.
     * So to speak if maxPoints is negative or not.
     */
    private boolean malus;

    /**
     * The default constructor for a requirement.
     * All the properties of this requirement have to be set manually after this instance is created.
     */
    public Requirement() {

    }

    /**
     * Creates a new {@link Requirement} with given properties.
     *
     * @param name             The name of the requirement which shall be short, descriptive and unique
     * @param description      A description of this requirement.
     * @param minMilestoneName The name of the {@link Milestone} upon this requirement is active
     * @param maxMilestoneName The name of the {@link Milestone} this requirement is active up to
     * @param maxPoints        The absolute, maximal amount of points this requirement can generate.
     * @param binary           Whether this requirement is binary (achieved: yes/no or partial).
     * @param mandatory        Whether this requirement is mandatory
     * @param malus            Whether this requirement has to be considered as a malus.
     */
    public Requirement(String name, String description, String minMilestoneName, String maxMilestoneName, double maxPoints, boolean binary, boolean mandatory, boolean malus) {
        this();

        this.name = name;
        this.description = description;
        this.minMilestoneName = minMilestoneName;
        this.maxMilestoneName = maxMilestoneName;
        this.maxPoints = maxPoints;
        this.binary = binary;
        this.mandatory = mandatory;
        this.malus = malus;
    }

    /**
     * Adds the given name of a requirement to the list of requirements this requirement depends on.
     * <p>
     * The corresponding {@link Requirement} with the given name is then a predecessor of this requirement.
     *
     * @param name The name of the requirement this requirement depends on. Must be a valid requirement name.
     * @return {@code true} As specified in {@link List#add(Object)}
     * @see List#add(Object)
     */
    public boolean addPredecessorName(String name) {
        return predecessorNames.add(name);
    }

    /**
     * Removes the specified requirement name of the list of
     *
     * @param name The name of the requirement this requirement no longer depends on. Must be a valid requirement name.
     * @return {@code true} If the specified name was in the list of predecessors (and is now not anymore).
     */
    public boolean removePredecessorName(String name) {
        return predecessorNames.remove(name);
    }

    /**
     * Returns a copy of the predecessor list.
     *
     * The {@link List} returned is a copy and not referenced within this instance.
     * Thus modifying the returning list <b>will not be synced</b> with the list of this instance.
     * To modify the list of predecessors use the appropriate methods provided by {@link Requirement}
     *
     * @return A copy of the list of predecessor names.
     */
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
