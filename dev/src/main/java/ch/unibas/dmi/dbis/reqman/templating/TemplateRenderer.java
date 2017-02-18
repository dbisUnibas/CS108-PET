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

    public TemplateRenderer(){

    }

    public <E> String render(String template, E instance, Map<String, Field<E, ?>> fields){

        /*
        // Not working ?!
        String[] targets = fields.keySet().toArray(new String[0]);
        ArrayList<String> replacements = new ArrayList<>(targets.length);
        fields.entrySet().forEach(entry -> {
            Field<E, ?> field = entry.getValue();
            replacements.add(field.render(instance));
            System.out.println(field.getName()+": "+field.render(instance));
        });

        return StringUtils.replaceEach(template, targets, replacements.toArray(new String[0]));
        */

        StringBuilder out = new StringBuilder(template);
        fields.forEach((variable, field)->{
            System.out.println(variable+": "+field.render(instance));
            Pattern p = Pattern.compile(variable);
            Matcher m = p.matcher(template);
            while(m.find() ){
                int b = m.start();
                int e = m.end();
                out.replace(m.start(), m.end(), field.render(instance));
            }
        });
        return out.toString();
    }
}
