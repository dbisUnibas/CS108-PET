package ch.unibas.dmi.dbis.reqman.templating;

import java.util.function.Function;

/**
 * Class to use for boolean fields and then depending on the value of the boolean either render something or something else.
 *
 * @author loris.sauter
 */
public class ConditionalField<E> extends  Field<E,Boolean> {

    private Function<Boolean, String> falseRenderer = null;

    /**
     *
     * @param name
     * @param getter
     * @param trueRenderer It is ensured, that the passed object to render is TRUE
     * @param falseRenderer It is ensured, that the passed object to render is FALSE
     */
    public ConditionalField(String name, Function<E,Boolean> getter, Function<Boolean,String> trueRenderer, Function<Boolean, String> falseRenderer){
        super(name, Type.CONDITIONAL, getter, trueRenderer);
        this.falseRenderer = falseRenderer;
    }

    public Function<Boolean, String> getTrueRenderer(){
        return getRenderer();
    }

    public Function<Boolean, String> getFalseRenderer(){
        return falseRenderer;
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
}
