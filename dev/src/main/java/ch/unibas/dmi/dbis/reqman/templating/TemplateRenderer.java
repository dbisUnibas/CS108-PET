package ch.unibas.dmi.dbis.reqman.templating;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class TemplateRenderer {


    private TemplateParser parser;

    public TemplateRenderer(TemplateParser parser){
        this.parser = parser;
    }

    /**
     * MAP must have the REGEX escaped value in it!
     * @param template
     * @param instance
     * @param fields
     * @param <E>
     * @return
     */
    public <E> String render(String template, E instance, Map<String, Field<E, ?>> fields){

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
                    out.replace(m.start(), m.end(), f.render(field.getGetter().apply(instance)));
                    */
                }else{
                    out.replace(m.start(), m.end(), field.render(instance));
                }
            }
        });
        return out.toString();
    }
}
