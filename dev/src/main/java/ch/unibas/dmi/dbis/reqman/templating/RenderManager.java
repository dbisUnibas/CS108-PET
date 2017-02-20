package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.core.Catalogue;
import ch.unibas.dmi.dbis.reqman.core.Milestone;
import ch.unibas.dmi.dbis.reqman.core.Requirement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class RenderManager {
    private static Logger LOGGER = LogManager.getLogger(RenderManager.class);

    private Template<Requirement> templateReq = null;

    private Template<Milestone> templateMS = null;

    private Template<Catalogue> templateCat = null;

    private TemplateParser parser = null;

    private TemplateRenderer renderer = null;

    private Catalogue catalogue = null;

    //TODO Add Support for Group

    public RenderManager(Catalogue catalogue){
        this.catalogue = catalogue;
        parser = new TemplateParser(catalogue);
        renderer = new TemplateRenderer();
    }

    public void setTemplateReq(Template<Requirement> templateReq) {
        this.templateReq = templateReq;
    }

    public void setTemplateMS(Template<Milestone> templateMS) {
        this.templateMS = templateMS;
    }

    public void setTemplateCat(Template<Catalogue> templateCat) {
        this.templateCat = templateCat;
    }

    public String renderMilestone(Milestone ms){
        return renderCarefully(renderer, templateMS, ms);
    }

    public String renderRequirement(Requirement req){
        return renderCarefully(renderer, templateReq, req);
    }

    public String renderCatalogue(){
        return renderCarefully(renderer, templateCat, catalogue);
    }

    public void parseRequirementTemplate(String reqTemplate){
        parseTemplateCarefully(REQUIREMENT_ENTITY, reqTemplate);
    }

    public void parseMilestoneTemplate(String msTemplate){
        parseTemplateCarefully(MILESTONE_ENTITY, msTemplate);
    }

    public void parseCatalogueTemplate(String catTemplate){
        parseTemplateCarefully(CATALOGUE_ENTITY, catTemplate);
    }

    private <E> boolean parseTemplateCarefully(Entity<E> entity, String template){
        parser.setupFor(entity);
        if(MILESTONE_ENTITY.getEntityName().equals(entity.getEntityName() ) ){
            // Template MS
            templateMS = parser.parseTemplate(template);
        }else if(REQUIREMENT_ENTITY.getEntityName().equals(entity.getEntityName() ) ){
            // Template Req
            templateReq = parser.parseTemplate(template);
        }else if(CATALOGUE_ENTITY.getEntityName().equals(entity.getEntityName() ) ){
            // Template Cat
            templateCat = parser.parseTemplate(template);
        }else{
            throw LOGGER.throwing( new UnsupportedOperationException("Cannot parse a template for entity: "+entity.toString()) );
        }
        return true;
    }

    private static <E> String renderCarefully(TemplateRenderer renderer, Template<E> template, E instance) throws IllegalArgumentException, IllegalStateException{
        if(renderer == null){
            throw new IllegalArgumentException("Cannot render if the passed TemplateRenderer is null");
        }
        if(instance == null){
            throw new IllegalArgumentException("Cannot render an instance if the instance is null");
        }

        if(template != null){
            return renderer.render(template, instance);
        }else{
            throw new IllegalStateException(String.format("Cannot render the instance [%s], if no template exists for.", instance));
        }
    }

    public final Entity<Milestone> MILESTONE_ENTITY = new Entity<Milestone>("milestone",
            new Field<Milestone, String>("name", Field.Type.NORMAL, Milestone::getName),
            new Field<Milestone, Date>("date", Field.Type.OBJECT, Milestone::getDate, date -> {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                return format.format(date);
            }),
            new ParametrizedField<Milestone, Date>("dateFormatted", Milestone::getDate) {
                private final Logger LOGGER = LogManager.getLogger(TemplateParser.class);
                @Override
                public String render(Milestone instance) {
                    try{
                        SimpleDateFormat format = new SimpleDateFormat(getParameter() );
                        return format.format(getGetter().apply(instance));
                    }catch(IllegalArgumentException iae){
                        LOGGER.error("The specified pattern is not compliant with java.text.SimpleDateFormat.", iae);
                    }
                    return "";
                }
            },
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
            }),
            new ConditionalField<Requirement>("binary", Requirement::isBinary, b-> "BINARY", b-> "PARTIAL"),
            new ConditionalField<Requirement>("mandatory",Requirement::isMandatory, b->"MANDATORY", b->"BONUS"),
            new ConditionalField<Requirement>("malus",Requirement::isMalus, b->"-",b->"+"),
            new ParametrizedField<Requirement, Map<String,String>>("meta", Requirement::getPropertiesMap){
                //private final Logger LOGGER = LogManager.getLogger(TemplateParser.class.getName().replace("TemplateParser", "MetaParser"));
                private final Logger LOGGER = LogManager.getLogger(TemplateParser.class);

                @Override
                public String render(Requirement instance) {
                    Map<String, String> map = getGetter().apply(instance);
                    if(!map.containsKey(getParameter())){
                        LOGGER.error(String.format("Error while parsing meta of requirement [name=%s]: There is no meta with name: %s", instance.getName(), getParameter() ));
                        return "";
                    }
                    String value = map.get(getParameter() );
                    if(value != null){
                        return value;
                    }else{
                        return "";
                    }
                }
            }
    );

    public final Entity<Catalogue> CATALOGUE_ENTITY = new Entity<Catalogue>("catalogue",
            new Field<Catalogue, String>("name", Field.Type.NORMAL, Catalogue::getName),
            new Field<Catalogue, String>("description", Field.Type.NORMAL, Catalogue::getDescription),
            new Field<Catalogue, String>("lecture", Field.Type.NORMAL, Catalogue::getLecture),
            new Field<Catalogue, String>("semester", Field.Type.NORMAL, Catalogue::getSemester),
            new Field<Catalogue, List<Requirement>>("requirements", Field.Type.LIST, Catalogue::getRequirements, (list)->{
                StringBuilder sb = new StringBuilder();
                list.forEach(req -> {
                    sb.append(renderRequirement(req));
                });
                return sb.toString();
            }),
            new Field<Catalogue, List<Milestone>>("milestones", Field.Type.LIST, Catalogue::getMilestones, (list) -> {
                StringBuilder sb = new StringBuilder();
                list.forEach(ms -> sb.append(renderMilestone(ms)));
                return sb.toString();
            })
    );
}
