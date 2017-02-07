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

    public Milestone(){

    }

    public Milestone(String name, int ordinal, Date date){
        this.name = name;
        this.ordinal = ordinal;
        this.date = date;
    }

    public int getOrdinal() {
        return ordinal;
    }

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
}
