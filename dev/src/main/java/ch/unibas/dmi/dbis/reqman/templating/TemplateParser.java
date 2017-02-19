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


    public static final String INDICATOR_REGEX = "\\$\\{";
    public static final String CLOSING_REGEX = "\\}";
    public static final String FIELD_DELIMETER_REGEX = "\\.";
    public static final String NAME_REGEX = "[a-zA-Z\\-]+";

    public static final String INDICATOR = "${";
    public static final String CLOSING = "}";
    public static final String FIELD_DELIMETER = ".";

    private Entity entity;

    private String searchOpen;

    private Pattern pattern;

    private Catalogue catalogue;
    // TODO: TemplateParser for Groups will need Group AND Catalogue.

    public TemplateParser(Catalogue cat){
        catalogue = cat;
    }

    public void setupFor(Entity entity){
        this.entity = entity;
        searchOpen = INDICATOR_REGEX + this.entity.getIndicatorName() + FIELD_DELIMETER_REGEX + NAME_REGEX ;
        pattern = Pattern.compile(searchOpen + CLOSING_REGEX);
    }


    public <E> Map<String, Field<E, ?>> parse(String template){
        // DIRECT FIELDS
        Map<String, Field<E, ?>> map = new TreeMap<>();
        Matcher matcher = pattern.matcher(template);
        while(matcher.find() ){
            String variable = template.substring(matcher.start(), matcher.end() );

            Field<E, ?> field = parseField(variable);

            String currentPatternOpen = INDICATOR_REGEX +entity.getIndicatorName()+ FIELD_DELIMETER_REGEX +field.getName();
            map.put(currentPatternOpen+ CLOSING_REGEX, field); // need to escape regex predifned dollar symbol
            //map.put(variable, field);

        }

        // ENTITY FIELDS
        Pattern p = Pattern.compile(searchOpen+ FIELD_DELIMETER_REGEX +NAME_REGEX+ CLOSING_REGEX);
        Matcher m = p.matcher(template);
        while(m.find() ){
            String completeVariable = template.substring(m.start(), m.end());
            int firstDelim = completeVariable.indexOf(FIELD_DELIMETER);
            int nextDelim = completeVariable.indexOf(FIELD_DELIMETER, firstDelim+1);
            String first = completeVariable.substring(firstDelim+1, nextDelim);
            String next = completeVariable.substring(nextDelim+1, completeVariable.lastIndexOf(CLOSING));
            Field<E, ?> f = parseField(INDICATOR+entity.getIndicatorName()+FIELD_DELIMETER+first+CLOSING);
            if(f.getType() == Field.Type.ENTITY){
                Entity sub = f.getSubEntity();
                if(sub.hasField(next)){
                    f.setSubFieldName(next);
                    map.put(INDICATOR_REGEX+entity.getIndicatorName()+FIELD_DELIMETER_REGEX+first+FIELD_DELIMETER_REGEX+next+CLOSING,f);
                }
            }
        }


        return map;
    }

    private <E> Field<E, ?> parseField(String field){
        Pattern p = Pattern.compile(FIELD_DELIMETER_REGEX +NAME_REGEX+ CLOSING_REGEX);
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

    public final Entity<Milestone> MILESTONE_ENTITY = new Entity<Milestone>("milestone",
            new Field<Milestone, String>("name", Field.Type.RAW, Milestone::getName),
            new Field<Milestone, Date>("date", Field.Type.OBJECT, Milestone::getDate, date -> {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                return format.format(date);
            }),
            new Field<Milestone, Integer>("ordinal", Field.Type.RAW, Milestone::getOrdinal)
    );

    public final Entity<Requirement> REQUIREMENT_ENTITY = new Entity<Requirement>("requirement",
            new Field<Requirement, String>("name", Field.Type.RAW, Requirement::getName),
            new Field<Requirement, String>("description", Field.Type.RAW, Requirement::getDescription),
            new Field<Requirement, Double>("maxPoints", Field.Type.RAW, Requirement::getMaxPoints),
            new Field<Requirement, Milestone>("minMS", Field.Type.ENTITY, req -> {
                return catalogue.getMilestoneByOrdinal(req.getMinMilestoneOrdinal());
            }, MILESTONE_ENTITY),
            new Field<Requirement, Milestone>("maxMS", Field.Type.ENTITY, req -> {
                return catalogue.getMilestoneByOrdinal(req.getMaxMilestoneOrdinal() );
            }, MILESTONE_ENTITY),
            new Field<Requirement, List<String>>("predecessorNames", Field.Type.OBJECT, Requirement::getPredecessorNames, list -> {
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





}
