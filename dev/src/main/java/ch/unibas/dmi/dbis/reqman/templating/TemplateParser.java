package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;
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
    public static final String OPTION_OPENING_REGEX = "\\[";
    public static final String OPTION_CLOSING_REGEX = "\\]";


    public static final String INDICATOR = "${";
    public static final String CLOSING = "}";
    public static final String FIELD_DELIMETER = ".";
    public static final String OPTION_OPENING = "[";
    public static final String OPTION_CLOSING = "]";

    private static final Logger LOGGER = LogManager.getLogger(TemplateParser.class);

    private Entity entity;

    @Deprecated
    private String searchOpen;

    private Pattern pattern;
    private String regexEntity;

    private Catalogue catalogue;
    // TODO: TemplateParser for Groups will need Group AND Catalogue.

    public TemplateParser(Catalogue cat){
        catalogue = cat;
    }

    public void setupFor(Entity entity){
        this.entity = entity;
        searchOpen = INDICATOR_REGEX + this.entity.getEntityName() + FIELD_DELIMETER_REGEX + NAME_REGEX ;
        pattern = Pattern.compile(searchOpen + CLOSING_REGEX);
        regexEntity = INDICATOR_REGEX + entity.getEntityName();
    }

    // TODO: Define visibility
    <E> List<Replacement<E>> parseReplacements(String template){
        LOGGER.trace("parseReplacements");
        ArrayList<Replacement<E>> list = new ArrayList();
        Pattern patternField = Pattern.compile(regexEntity+FIELD_DELIMETER_REGEX+NAME_REGEX);
        LOGGER.debug(String.format("[parseRep] Field regex: %s", patternField.pattern()));
        Matcher matcherField = patternField.matcher(template);
        while(matcherField.find() ){
            String expression = template.substring(matcherField.start(), matcherField.end() );
            String successor = template.substring(matcherField.end(), matcherField.end()+1);
            LOGGER.debug("[parseRep] Expression: "+expression);
            LOGGER.debug("[parseRep] Expression region: "+matcherField.start()+", "+matcherField.end() );
            LOGGER.debug("[parseRep] Sucessor: "+successor);
            switch (successor){
                case CLOSING:
                    // normal field
                    Field<E, ?> field = parseNormalField(expression+CLOSING);
                    Replacement<E> repl = new Replacement<E>(field,matcherField.start(), matcherField.end(), regexEntity+FIELD_DELIMETER_REGEX+field.getName()+CLOSING_REGEX, expression+CLOSING);
                    list.add(repl);
                    LOGGER.debug(String.format("[parseRep] Replacement: %s", repl.toString()));
                    break;
                case FIELD_DELIMETER:
                    // subentity field
                    int end = template.indexOf(CLOSING, matcherField.end());
                    String subExpression = template.substring(matcherField.start(), end+1);
                    SubEntityField<E, ?> subField = parseSubField(patternField.pattern(),  subExpression);
                    Replacement<E> subRepl = new Replacement<E>(subField, matcherField.start(), end+1, regexEntity+FIELD_DELIMETER_REGEX+subField.getName()+FIELD_DELIMETER_REGEX+subField.getSubFieldName()+CLOSING_REGEX, subExpression);
                    list.add(subRepl);
                    LOGGER.debug(String.format("[parseRep] Replacement: %s", subRepl.toString() ) );
                    break;
                case OPTION_OPENING:
                    // conditional OR advanced
                    break;
                default:
                    // something went wrong
                    break;
            }
        }

        return list;
    }

    private <E> SubEntityField<E, ?> parseSubField(String regexField, String expression){
        LOGGER.debug("[parseSubEntity] Expression: "+expression);
        Pattern patternSub = Pattern.compile(regexField+FIELD_DELIMETER_REGEX+NAME_REGEX);
        LOGGER.debug("[parseSubEntity] Sub rexeg: "+patternSub.pattern());
        Matcher matcherSub = patternSub.matcher(expression);
        while(matcherSub.find() ){
            String found = expression.substring(matcherSub.start(), matcherSub.end() );
            LOGGER.debug("[parseSubEntity] Found: "+found);
            int firstFieldDelim = found.indexOf(FIELD_DELIMETER);
            int nextFieldDelim = found.indexOf(FIELD_DELIMETER, firstFieldDelim+1);
            LOGGER.debug("[parseSubEntity] First delim: "+firstFieldDelim+", next: "+nextFieldDelim);
            String entityFieldName = found.substring(firstFieldDelim+1, nextFieldDelim);
            String subFieldName = found.substring(nextFieldDelim+1, matcherSub.end());
            String successor = expression.substring(matcherSub.end(), matcherSub.end() +1);
            LOGGER.debug("[parseSubEntity] entityFieldName: "+entityFieldName);
            LOGGER.debug("[parseSubEntity] subFieldName: "+subFieldName);
            LOGGER.debug("[parseSubEntity] successor: "+successor);
            if(CLOSING.equals(successor)){
                // Done
                SubEntityField<E, ?> subField = (SubEntityField<E,?>)entity.getFieldForName(entityFieldName);
                subField.setSubFieldName(subFieldName);
                return subField;
            }else{
                // parse options?
                // TODO: implement
                throw new UnsupportedOperationException("Parsing on such a deep level not yet supported");
            }
        }
        return null;
    }







    @Deprecated
    public <E> Map<String, Field<E, ?>> oldParse(String template){
        // DIRECT FIELDS
        Map<String, Field<E, ?>> map = new TreeMap<>();
        Matcher matcher = pattern.matcher(template);
        while(matcher.find() ){
            String variable = template.substring(matcher.start(), matcher.end() );

            Field<E, ?> field = parseNormalField(variable);

            String currentPatternOpen = INDICATOR_REGEX +entity.getEntityName()+ FIELD_DELIMETER_REGEX +field.getName();
            map.put(currentPatternOpen+ CLOSING_REGEX, field); // need to escape regex predifned dollar symbol
            //map.put(variable, field);

        }

        // SUB_ENTITY FIELDS
        Pattern p = Pattern.compile(searchOpen+ FIELD_DELIMETER_REGEX +NAME_REGEX+ CLOSING_REGEX);
        Matcher m = p.matcher(template);
        while(m.find() ){
            String completeVariable = template.substring(m.start(), m.end());
            int firstDelim = completeVariable.indexOf(FIELD_DELIMETER);
            int nextDelim = completeVariable.indexOf(FIELD_DELIMETER, firstDelim+1);
            String first = completeVariable.substring(firstDelim+1, nextDelim);
            String next = completeVariable.substring(nextDelim+1, completeVariable.lastIndexOf(CLOSING));
            Field<E, ?> f = parseNormalField(INDICATOR+entity.getEntityName()+FIELD_DELIMETER+first+CLOSING);
            /*
            if(f.getType() == Field.Type.SUB_ENTITY){
                Entity sub = f.getSubEntity();
                if(sub.hasField(next)){
                    f.setSubFieldName(next);
                    map.put(INDICATOR_REGEX+entity.getEntityName()+FIELD_DELIMETER_REGEX+first+FIELD_DELIMETER_REGEX+next+CLOSING,f);
                }
            }
            */
        }


        return map;
    }

    private <E> Field<E, ?> parseNormalField(String expression){
        Pattern p = Pattern.compile(FIELD_DELIMETER_REGEX +NAME_REGEX+ CLOSING_REGEX);
        LOGGER.debug("[parseNormal] Field regex: "+p.pattern() );
        Matcher matcher = p.matcher(expression);
        if(matcher.find() ){
            String name = expression.substring(matcher.start()+1, matcher.end()-1);
            LOGGER.debug("[parseNormal] Name: "+name);
            if(entity.hasField(name)){
                return entity.getFieldForName(name);
            }else{
                throw new ParseException("Entity ("+entity.getEntityName()+") has no field with name "+name+" registered");// TODO write own exception (for better handling)
            }
        }
        throw new ParseException("Found undefined expression: "+expression);
    }

    public static class ParseException extends RuntimeException{
        public ParseException() {
        }

        public ParseException(String message) {
            super(message);
        }

        public ParseException(String message, Throwable cause) {
            super(message, cause);
        }

        public ParseException(Throwable cause) {
            super(cause);
        }

        public ParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    public final Entity<Milestone> MILESTONE_ENTITY = new Entity<Milestone>("milestone",
            new Field<Milestone, String>("name", Field.Type.NORMAL, Milestone::getName),
            new Field<Milestone, Date>("date", Field.Type.OBJECT, Milestone::getDate, date -> {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                return format.format(date);
            }),
            new Field<Milestone, Integer>("ordinal", Field.Type.NORMAL, Milestone::getOrdinal)
    );

    public final Entity<Requirement> REQUIREMENT_ENTITY = new Entity<Requirement>("requirement",
            new Field<Requirement, String>("name", Field.Type.NORMAL, Requirement::getName),
            new Field<Requirement, String>("description", Field.Type.NORMAL, Requirement::getDescription),
            new Field<Requirement, Double>("maxPoints", Field.Type.NORMAL, Requirement::getMaxPoints),
            new SubEntityField<Requirement, Milestone>("minMS", (requirement -> {
                return catalogue.getMilestoneByOrdinal(requirement.getMinMilestoneOrdinal());
            }), MILESTONE_ENTITY),
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
