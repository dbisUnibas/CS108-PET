package ch.unibas.dmi.dbis.reqman.templating;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

/**
 * Class to use for boolean fields and then depending on the value of the boolean either oldRender something or something else.
 *
 * @author loris.sauter
 */
public class ConditionalField<E> extends ParametrizedField<E, Boolean> {

    private static final Logger LOGGER = LogManager.getLogger(TemplateRenderer.class);

    /**
     * On runtime, this will contain the parsed false-string
     * e.g given expression: <code>${requirement.mandatory[mandatory][bonus]}</code>
     * this will return <code>bonus</code> during runtime.
     */
    private Function<Boolean, String> falseRenderer = null;

    /**
     * @param name
     * @param getter
     * @param defaultTrueRenderer  It is ensured, that the passed object to oldRender is TRUE. WILL BE ASSIGNED ON RUNTIME with real value.
     * @param defaultFalseRenderer It is ensured, that the passed object to oldRender is FALSE. WILL BE ASSIGNED ON RUNTIME with real value.
     */
    public ConditionalField(String name, Function<E, Boolean> getter, Function<Boolean, String> defaultTrueRenderer, Function<Boolean, String> defaultFalseRenderer) {
        super(name, Type.CONDITIONAL, getter, defaultTrueRenderer);
        this.falseRenderer = defaultFalseRenderer;
    }

    public static <E> ConditionalField<E> copy(ConditionalField<E> source) {
        ConditionalField<E> copy = new ConditionalField<E>(source.getName(), source.getGetter(), source.getTrueRenderer(), source.getFalseRenderer());
        return copy;
    }

    public Function<Boolean, String> getTrueRenderer() {
        return getRenderer();
    }

    public Function<Boolean, String> getFalseRenderer() {
        return falseRenderer;
    }

    public void setTrueRenderer(Function<Boolean, String> trueRenderer) {
        setRenderer(trueRenderer);
    }

    public void setFalseRenderer(Function<Boolean, String> falseRenderer) {
        this.falseRenderer = falseRenderer;
    }

    @Override
    public String render(E instance) {
        LOGGER.trace(":render$Conditional");
        boolean fieldValue = getter.apply(instance);
        LOGGER.trace(":render$Conditional - Condition: "+fieldValue);
        LOGGER.trace(":render$Conditional - trueRenderer: "+getTrueRenderer().apply(fieldValue ) );
        LOGGER.trace(":render$Conditional - falseRenderer: "+getFalseRenderer().apply(fieldValue ) );
        if (fieldValue) {
            return getTrueRenderer().apply(fieldValue);
        } else {
            return getFalseRenderer().apply(fieldValue);
        }
    }

    /**
     * Passed directly to {@link ConditionalField#render(Object)}
     *
     * @param instance
     * @param param
     * @return
     */
    @Override
    public String renderCarefully(E instance, String param) {
        return render(instance);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ConditionalField{");
        sb.append("name='").append(name).append('\'');
        sb.append(", falseRenderer=").append(falseRenderer);
        sb.append(", trueRenderer=").append(getTrueRenderer());
        sb.append(", type='").append(getType()).append('\'');
        sb.append(", getter='").append(getGetter()).append('\'');
        sb.append(", renderer='").append(getRenderer()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
