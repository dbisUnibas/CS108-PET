package ch.unibas.dmi.dbis.reqman.templating;

import java.util.function.Function;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class SubEntityField<E, T> extends Field<E, T> {

    public SubEntityField(String name, Function<E, T> getter, Entity<T> subEntity) {
        super(name, Type.SUB_ENTITY, getter);
        this.subEntity = subEntity;
    }

    private Entity<T> subEntity;
    public Entity<T> getSubEntity(){
        return subEntity;
    }

    private String subFieldName;
    public String getSubFieldName(){
        return subFieldName;
    }
    public void setSubFieldName(String name){
        subFieldName = name;
    }
}
