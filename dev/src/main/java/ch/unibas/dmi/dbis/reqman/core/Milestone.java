package ch.unibas.dmi.dbis.reqman.core;

import java.util.Date;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class Milestone {

    private int ordinal = 0;

    private String name;

    private Date date;

    public Milestone() {

    }

    public Milestone(String name, int ordinal, Date date) {
        this.name = name;
        this.ordinal = ordinal;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Milestone milestone = (Milestone) o;

        return this.getOrdinal() == milestone.getOrdinal();
    }

    @Override
    public int hashCode() {
        int result = getOrdinal();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        return result;
    }

    public int getOrdinal() {
        return ordinal;
    }

    // TODO reduce visibility
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Milestone{");
        sb.append("ordinal=").append(ordinal);
        sb.append(", name='").append(name).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
