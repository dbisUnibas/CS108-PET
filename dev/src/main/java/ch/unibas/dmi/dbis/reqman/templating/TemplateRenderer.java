package ch.unibas.dmi.dbis.reqman.templating;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class TemplateRenderer {

    private static final Logger LOGGER = LogManager.getLogger(TemplateRenderer.class);

    @Deprecated // Not used
    private TemplateParser parser = null;

    @Deprecated
    public TemplateRenderer(TemplateParser parser){
        this.parser = parser;
    }

    public TemplateRenderer() {
    }

    public <E> String render(Template<E> template, E instance){
        LOGGER.debug("Rendering template for instance: "+instance.toString() );
        LOGGER.trace("Render template: "+template.getTemplate() );
        StringBuilder out = new StringBuilder(template.getTemplate() );

        template.getReplacements().forEach(replacement -> {
            Field<E, ?> field = replacement.getField();
            LOGGER.debug("Replacement: "+replacement.toString());
            int calcStart = out.indexOf(replacement.getTargetExpression() );
            int calcEnd = calcStart+replacement.getTargetExpression().length();
            LOGGER.debug("Calculated region: <"+calcStart+","+calcEnd+">");
            LOGGER.trace("PreReplacement: "+out.toString());
            String repl = field.render(instance);
            if(field instanceof ParametrizedField){
                repl = ((ParametrizedField) field).renderCarefully(instance, ((ParametrizedField) field).getParameter());
            }
            out.replace(calcStart, calcEnd, repl);

            LOGGER.trace("PostReplacement: "+out.toString());

            /*
            // Below code not needed, child classes of Field do override render
            if(field instanceof  SubEntityField){
                SubEntityField sub = (SubEntityField)field;

            }else if(field instanceof ConditionalField){
                ConditionalField cond = (ConditionalField)field;
            }else{
                out.replace(replacement.getStart(), replacement.getEnd(), field.render(instance));
            }*/
        });

        return out.toString();
    }


    /**
     * MAP must have the REGEX escaped value in it!
     * @param template
     * @param instance
     * @param fields
     * @param <E>
     * @return
     */
    @Deprecated
    public <E> String oldRender(String template, E instance, Map<String, Field<E, ?>> fields){

        StringBuilder out = new StringBuilder(template);
        fields.forEach((variable, field)->{
            Pattern p = Pattern.compile(variable);
            Matcher m = p.matcher(out.toString());
            while(m.find() ){
                if(field.getType() == Field.Type.SUB_ENTITY){
                    /*
                    Entity sub = field.getSubEntity();
                    Field f = sub.getFieldForName(field.getSubFieldName() );
                    Object o = field.getGetter().apply(instance);
                    System.out.println(o);
                    out.replace(m.start(), m.end(), f.oldRender(field.getGetter().apply(instance)));
                    */
                }else{
                    out.replace(m.start(), m.end(), field.render(instance));
                }
            }
        });
        return out.toString();
    }


}
