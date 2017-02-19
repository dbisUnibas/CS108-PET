package ch.unibas.dmi.dbis.reqman.templating;

import java.util.function.Function;

/**
 * Class to use for boolean fields and then depending on the value of the boolean either oldRender something or something else.
 *
 * @author loris.sauter
 */
public class ConditionalField<E> extends  Field<E,Boolean> {

    /**
     * On runtime, this will contain the parsed false-string
     * e.g given expression: <code>${requirement.mandatory[mandatory][bonus]}</code>
     * this will return <code>bonus</code> during runtime.
     */
    private Function<Boolean, String> falseRenderer = null;

    /**
     *
     * @param name
     * @param getter
     * @param defaultTrueRenderer It is ensured, that the passed object to oldRender is TRUE. WILL BE ASSIGNED ON RUNTIME with real value.
     * @param defaultFalseRenderer It is ensured, that the passed object to oldRender is FALSE. WILL BE ASSIGNED ON RUNTIME with real value.
     */
    public ConditionalField(String name, Function<E,Boolean> getter, Function<Boolean,String> defaultTrueRenderer, Function<Boolean, String> defaultFalseRenderer){
        super(name, Type.CONDITIONAL, getter, defaultTrueRenderer);
        this.falseRenderer = defaultFalseRenderer;
    }

    public Function<Boolean, String> getTrueRenderer(){
        return getRenderer();
    }

    public Function<Boolean, String> getFalseRenderer(){
        return falseRenderer;
    }

    public void setTrueRenderer(Function<Boolean, String> trueRenderer){
        setRenderer(trueRenderer);
    }

    public void setFalseRenderer(Function<Boolean, String> falseRenderer) {
        this.falseRenderer = falseRenderer;
    }

    @Override
    public String render(E instance){
        boolean fieldValue = getter.apply(instance);
        if(fieldValue){
            return getTrueRenderer().apply(fieldValue);
        }else{
            return getFalseRenderer().apply(fieldValue);
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ConditionalField{");
        sb.append("falseRenderer=").append(falseRenderer);
        sb.append(", trueRenderer=").append(getTrueRenderer());
        sb.append(", type='").append(getType()).append('\'');
        sb.append(", getter='").append(getGetter() ).append('\'');
        sb.append(", renderer='").append(getRenderer() ).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
