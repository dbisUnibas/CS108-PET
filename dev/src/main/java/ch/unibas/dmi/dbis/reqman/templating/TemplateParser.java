package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class TemplateParser{


    public static final String INDICATOR = "\\$\\{";
    public static final String CLOSING = "\\}";

    public static final String FIELD_DELIMETER = "\\.";
    public static final String NAME_REGEX = "[a-z\\-]+";

    private Entity entity;

    private String searchRegex;

    private Pattern pattern;

    private Catalogue catalogue;
    // TODO: TemplateParser for Groups will need Group AND Catalogue.

    public TemplateParser(Catalogue cat){
        catalogue = cat;
    }

    public void setupFor(Entity entity){
        this.entity = entity;
        searchRegex = INDICATOR + this.entity.getIndicatorName() + FIELD_DELIMETER + NAME_REGEX + CLOSING;
        pattern = Pattern.compile(searchRegex);
        System.out.println("REGEX: "+pattern.pattern());
    }


    public <E> Map<String, Field<E, ?>> parse(String template){
        Map<String, Field<E, ?>> map = new TreeMap<>();
        Matcher matcher = pattern.matcher(template);
        while(matcher.find() ){
            String variable = template.substring(matcher.start(), matcher.end() );

            Field<E, ?> field = parseField(variable);

            map.put(INDICATOR+entity.getIndicatorName()+FIELD_DELIMETER+field.getName()+CLOSING, field); // need to escape regex predifned dollar symbol
            //map.put(variable, field);
        }

        return map;
    }

    private <E> Field<E, ?> parseField(String field){
        Pattern p = Pattern.compile(FIELD_DELIMETER+NAME_REGEX+CLOSING);
        Matcher matcher = p.matcher(field);
        if(matcher.find() ){
            String name = field.substring(matcher.start()+1, matcher.end()-1);
            if(entity.hasField(name)){
                return entity.getFieldForName(name);
            }else{
                throw new RuntimeException("Entity ("+entity.getIndicatorName()+") has no field with name "+name+" registered");// TODO write own exception (for better handling)
            }
        }
        return null; // Severe error
    }

    public final Entity<Requirement> REQUIREMENT_ENTITY = new Entity<Requirement>("requirement",
            new Field<Requirement, String>("name", Field.Type.RAW, Requirement::getName),
            new Field<Requirement, String>("description", Field.Type.RAW, Requirement::getDescription),
            new Field<Requirement, Double>("max-points", Field.Type.RAW, Requirement::getMaxPoints),
            new Field<Requirement, Milestone>("min-ms", Field.Type.ENTITY, req -> {
                return catalogue.getMilestoneByOrdinal(req.getMinMilestoneOrdinal());
            }),
            new Field<Requirement, Milestone>("max-ms", Field.Type.ENTITY, req -> {
                return catalogue.getMilestoneByOrdinal(req.getMaxMilestoneOrdinal() );
            }),
            new Field<Requirement, List<String>>("predecessor-names", Field.Type.OBJECT, Requirement::getPredecessorNames, list -> {
                StringBuilder sb = new StringBuilder();
                list.forEach(str -> {
                    sb.append(str);
                    sb.append(", ");
                });
                String out = sb.toString();
                if(out.charAt(out.length()-1) == ' '){
                    return out.substring(0, out.lastIndexOf(", "));
                }
                return out;
            })
    );

    public final Entity<Milestone> MILESTONE_ENTITY = new Entity<Milestone>("milestone",
            new Field<Milestone, String>("name", Field.Type.RAW, Milestone::getName),
            new Field<Milestone, Date>("date", Field.Type.OBJECT, Milestone::getDate, date -> {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                return format.format(date);
            }),
            new Field<Milestone, Integer>("ordinal", Field.Type.RAW, Milestone::getOrdinal)
    );



}
