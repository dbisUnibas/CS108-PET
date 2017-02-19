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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SubEntityField{");
        sb.append("subEntity=").append(subEntity!= null?subEntity.getEntityName():"null");
        sb.append(", name='").append(name).append('\'');
        sb.append(", subFieldName='").append(subFieldName).append('\'');
        sb.append(", type='").append(getType()).append('\'');
        sb.append(", getter='").append(getGetter() ).append('\'');
        sb.append(", renderer='").append(getRenderer() ).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SubEntityField<?, ?> that = (SubEntityField<?, ?>) o;

        if (subEntity != null ? !subEntity.equals(that.subEntity) : that.subEntity != null) {
            return false;
        }
        return subFieldName != null ? subFieldName.equals(that.subFieldName) : that.subFieldName == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (subEntity != null ? subEntity.hashCode() : 0);
        result = 31 * result + (subFieldName != null ? subFieldName.hashCode() : 0);
        return result;
    }

    @Override
    public String render(E instance){
        Field subField = subEntity.getFieldForName(subFieldName);
        return subField.render(getter.apply(instance));
    }
}
