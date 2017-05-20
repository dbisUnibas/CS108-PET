package ch.unibas.dmi.dbis.reqman.templating;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class TemplateParser {


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

    public TemplateParser() {
    }

    public void setupFor(Entity entity) {
        this.entity = entity;
        searchOpen = INDICATOR_REGEX + this.entity.getEntityName() + FIELD_DELIMETER_REGEX + NAME_REGEX;
        pattern = Pattern.compile(searchOpen + CLOSING_REGEX);
        regexEntity = INDICATOR_REGEX + entity.getEntityName();
    }

    public <E> Template<E> parseTemplate(String template) {
        if (entity == null) {
            throw new IllegalStateException("Parser not set up. One must invoke TemplateParser.setupFor(Entity) before parsing a template.");
        }
        List<Replacement<E>> list = parseReplacements(template);
        return new Template<E>(template, list, entity);
    }

    @Deprecated
    public <E> Map<String, Field<E, ?>> oldParse(String template) {
        // DIRECT FIELDS
        Map<String, Field<E, ?>> map = new TreeMap<>();
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String variable = template.substring(matcher.start(), matcher.end());

            Field<E, ?> field = parseNormalField(variable);

            String currentPatternOpen = INDICATOR_REGEX + entity.getEntityName() + FIELD_DELIMETER_REGEX + field.getName();
            map.put(currentPatternOpen + CLOSING_REGEX, field); // need to escape regex predifned dollar symbol
            //map.put(variable, field);

        }

        // SUB_ENTITY FIELDS
        Pattern p = Pattern.compile(searchOpen + FIELD_DELIMETER_REGEX + NAME_REGEX + CLOSING_REGEX);
        Matcher m = p.matcher(template);
        while (m.find()) {
            String completeVariable = template.substring(m.start(), m.end());
            int firstDelim = completeVariable.indexOf(FIELD_DELIMETER);
            int nextDelim = completeVariable.indexOf(FIELD_DELIMETER, firstDelim + 1);
            String first = completeVariable.substring(firstDelim + 1, nextDelim);
            String next = completeVariable.substring(nextDelim + 1, completeVariable.lastIndexOf(CLOSING));
            Field<E, ?> f = parseNormalField(INDICATOR + entity.getEntityName() + FIELD_DELIMETER + first + CLOSING);
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

    <E> List<Replacement<E>> parseReplacements(String template) {
        LOGGER.trace("parseReplacements");
        ArrayList<Replacement<E>> list = new ArrayList<>();
        Pattern patternField = Pattern.compile(regexEntity + FIELD_DELIMETER_REGEX + NAME_REGEX);
        LOGGER.debug(String.format("[parseRep] Field regex: %s", patternField.pattern()));
        Matcher matcherField = patternField.matcher(template);
        while (matcherField.find()) {
            String expression = template.substring(matcherField.start(), matcherField.end());
            String successor = template.substring(matcherField.end(), matcherField.end() + 1);
            LOGGER.debug("[parseRep] Expression: " + expression);
            LOGGER.debug("[parseRep] Expression region: " + matcherField.start() + ", " + matcherField.end());
            LOGGER.debug("[parseRep] Sucessor: " + successor);
            int end = template.indexOf(CLOSING, matcherField.end());
            String subExpression = template.substring(matcherField.start(), end + 1);
            switch (successor) {
                case CLOSING:
                    // normal field
                    Field<E, ?> field = parseNormalField(expression + CLOSING);
                    Replacement<E> repl = new Replacement<E>(field, matcherField.start(), matcherField.end() + 1, regexEntity + FIELD_DELIMETER_REGEX + field.getName() + CLOSING_REGEX, expression + CLOSING);
                    list.add(repl);
                    break;
                case FIELD_DELIMETER:
                    // subentity field
                    SubEntityField<E, ?> subField = parseSubField(patternField.pattern(), subExpression);
                    Replacement<E> subRepl = new Replacement<E>(subField, matcherField.start(), end + 1, regexEntity + FIELD_DELIMETER_REGEX + subField.getName() + FIELD_DELIMETER_REGEX + subField.getSubFieldName() + CLOSING_REGEX, subExpression);
                    list.add(subRepl);
                    break;
                case OPTION_OPENING:
                    // CONDITIONAL or PARAMETRIZED
                    Field<E, ?> paramField = parseParametrizedField(patternField.pattern(), subExpression);
                    Replacement<E> paramRepl = new Replacement<E>(paramField, matcherField.start(), end + 1, "", subExpression);
                    list.add(paramRepl);
                    break;
                default:
                    // something went wrong
                    LOGGER.error("FOUND something that is not handled: " + successor);
                    break;
            }
            LOGGER.debug(String.format("[parseRep] Added replacement: %s", list.get(list.size() - 1).toString()));
        }

        return list;
    }

    private <E> SubEntityField<E, ?> parseSubField(String regexField, String expression) {
        LOGGER.debug("[parseSubEntity] Expression: " + expression);
        Pattern patternSub = Pattern.compile(regexField + FIELD_DELIMETER_REGEX + NAME_REGEX);
        LOGGER.debug("[parseSubEntity] Sub rexeg: " + patternSub.pattern());
        Matcher matcherSub = patternSub.matcher(expression);
        while (matcherSub.find()) {
            String found = expression.substring(matcherSub.start(), matcherSub.end());
            LOGGER.debug("[parseSubEntity] Found: " + found);
            int firstFieldDelim = found.indexOf(FIELD_DELIMETER);
            int nextFieldDelim = found.indexOf(FIELD_DELIMETER, firstFieldDelim + 1);
            LOGGER.debug("[parseSubEntity] First delim: " + firstFieldDelim + ", next: " + nextFieldDelim);
            String entityFieldName = found.substring(firstFieldDelim + 1, nextFieldDelim);
            String subFieldName = found.substring(nextFieldDelim + 1, matcherSub.end());
            String successor = expression.substring(matcherSub.end(), matcherSub.end() + 1);
            LOGGER.debug("[parseSubEntity] entityFieldName: " + entityFieldName);
            LOGGER.debug("[parseSubEntity] subFieldName: " + subFieldName);
            LOGGER.debug("[parseSubEntity] successor: " + successor);
            if (CLOSING.equals(successor)) {
                // Done
                SubEntityField<E, ?> subField = (SubEntityField<E, ?>) entity.getFieldForName(entityFieldName);
                subField.setSubFieldName(subFieldName);
                return SubEntityField.copy(subField);
            } else {
                // parse options?
                // TODO: implement
                UnsupportedOperationException uoe = new UnsupportedOperationException("Parsing on such a deep level not yet supported");
                LOGGER.throwing(uoe);
                throw uoe;
            }
        }
        return null;
    }

    /**
     * Used to EITHER parse CONDITIONAL and PARAMETRIZED fields.
     *
     * @param regexField
     * @param expression
     * @param <E>
     * @return
     */
    private <E> Field<E, ?> parseParametrizedField(String regexField, String expression) {
        LOGGER.debug("[parseParametrized] Expression: " + expression);
        Pattern patternSub = Pattern.compile(regexField);
        LOGGER.debug("[parseParametrized] Field rexeg: " + patternSub.pattern());
        Matcher matcherSub = patternSub.matcher(expression);
        while (matcherSub.find()) {
            String found = expression.substring(matcherSub.start(), matcherSub.end());
            LOGGER.debug("[parseParametrized] Found: " + found);
            String fieldName = found.substring(found.indexOf(FIELD_DELIMETER) + 1);
            LOGGER.debug("[parseParametrized] FieldName: " + fieldName);
            if (entity.hasField(fieldName)) {
                Field<E, ?> field = entity.getFieldForName(fieldName);
                if (field instanceof ConditionalField) {
                    // CASE Missing option_closing:
                    int firstClosing = expression.indexOf(OPTION_CLOSING);
                    if (firstClosing == -1) {
                        throw new ParseException("Missing " + OPTION_CLOSING + " near: " + expression);
                    }
                    // CASE parameter1 provided
                    int firstOpening = expression.indexOf(OPTION_OPENING);
                    String param1 = expression.substring(firstOpening + 1, firstClosing);
                    LOGGER.debug("[parseParametrized] Found param1: " + param1);
                    Function<Boolean, String> trueRenderer = b -> param1;
                    // CASE NO parameter2 provided
                    int secondClosing = expression.lastIndexOf(OPTION_CLOSING);
                    Function<Boolean, String> falseRenderer = b -> "";
                    if (secondClosing == firstClosing) {
                        LOGGER.warn("No secondary parameter provided. The falseRenderer will oldRender an empty string. Expression: " + expression);
                    }
                    int secondOpening = expression.lastIndexOf(OPTION_OPENING);
                    if (secondOpening == -1) {
                        throw new ParseException("Missing " + OPTION_OPENING + " near: " + expression);
                    }
                    String param2 = expression.substring(secondOpening + 1, secondClosing);
                    LOGGER.debug("[parseParametrized] Found param2: " + param2);
                    falseRenderer = b -> param2;
                    ConditionalField<E> condField = (ConditionalField) field;
                    condField.setTrueRenderer(trueRenderer);
                    condField.setFalseRenderer(falseRenderer);
                    return ConditionalField.copy(condField);
                } else if (field instanceof ParametrizedField) {
                    // CASE Missing option_closing:
                    int firstClosing = expression.indexOf(OPTION_CLOSING);
                    if (firstClosing == -1) {
                        throw new ParseException("Missing " + OPTION_CLOSING + " near: " + expression);
                    }
                    // CASE parameter1 provided
                    int firstOpening = expression.indexOf(OPTION_OPENING);
                    String param = expression.substring(firstOpening + 1, firstClosing);
                    LOGGER.debug("[parseParametrized] Found param: " + param);
                    ParametrizedField parField = (ParametrizedField) field;
                    ParametrizedField copy = ParametrizedField.copy(parField);
                    copy.setParameter(param);
                    return copy;
                } else {
                    LOGGER.warn(String.format("Field [%s] of entity [%s] is not parametrized. Ignoring those parameters.", fieldName, entity.getEntityName()));
                    return field; // Has parameter even no parameters are allowed: Ignore those parameters.
                }
            } else {
                throwNoSuchField(found);
            }
        }


        return null;
    }

    private <E> Field<E, ?> parseNormalField(String expression) {
        Pattern p = Pattern.compile(FIELD_DELIMETER_REGEX + NAME_REGEX + CLOSING_REGEX);
        LOGGER.debug("[parseNormal] Field regex: " + p.pattern());
        Matcher matcher = p.matcher(expression);
        if (matcher.find()) {
            String name = expression.substring(matcher.start() + 1, matcher.end() - 1);
            LOGGER.debug("[parseNormal] Name: " + name);
            if (entity.hasField(name)) {
                return entity.getFieldForName(name);
            } else {
                throwNoSuchField(name);
            }
        }
        throw new ParseException("Found undefined expression: " + expression);
    }

    private void throwNoSuchField(String name) throws ParseException {
        throw new ParseException("Entity (" + entity.getEntityName() + ") has no field with name " + name + " registered");
    }

    public static class ParseException extends RuntimeException {
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
}
