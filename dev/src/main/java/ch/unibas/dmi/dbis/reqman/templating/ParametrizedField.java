package ch.unibas.dmi.dbis.reqman.templating;

import java.util.function.Function;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public abstract class ParametrizedField<E, T> extends Field<E, T> {

    private String parameter;

    public ParametrizedField(String name, Function<E, T> getter) {
        super(name, Type.PARAMETRIZED, getter);
    }

    protected ParametrizedField(String name, Type type, Function<E, T> getter, Function<T, String> renderer) {
        super(name, type, getter, renderer);
    }

    public static <E, T> ParametrizedField<E, T> copy(ParametrizedField<E, T> source) {
        ParametrizedField<E, T> copy = new ParametrizedField<E, T>(source.getName(), source.getGetter()) {
            @Override
            public String renderCarefully(E instance, String parameter) {
                return source.renderCarefully(instance, parameter);
            }
        };

        return copy;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ParametrizedField{");
        sb.append("parameter='").append(parameter).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", type=").append(type);
        sb.append(", getter=").append(getter);
        sb.append(", renderer=").append(renderer);
        sb.append('}');
        return sb.toString();
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public String render(E instance) {
        return renderCarefully(instance, parameter);
    }

    public abstract String renderCarefully(E instance, String parameter);
}
