package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.common.SortingUtils;
import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.net.ProgressEvent;

import java.text.SimpleDateFormat;
import java.util.Comparator;
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
    private Template<Milestone> templateGroupMS = null;
    private Template<Group> templateGroup = null;
    private Template<Progress> templateProgress = null;
    private TemplateParser parser = null;
    private TemplateRenderer renderer = null;
    /**
     * Existing:
     * .name
     * .description
     * .lecture
     * .semester
     * .requirements
     * .milestones
     * .sumTotal
     * .sumMS[<ordinal>]
     * .milestoneName[<ordinal>]
     */
    private final Entity<Catalogue> CATALOGUE_ENTITY = new Entity<Catalogue>("catalogue",
            new Field<Catalogue, String>("name", Field.Type.NORMAL, Catalogue::getName),
            new Field<Catalogue, String>("description", Field.Type.NORMAL, Catalogue::getDescription),
            new Field<Catalogue, String>("lecture", Field.Type.NORMAL, Catalogue::getLecture),
            new Field<Catalogue, String>("semester", Field.Type.NORMAL, Catalogue::getSemester),
            new Field<Catalogue, List<Requirement>>("requirements", Field.Type.LIST, Catalogue::getRequirements, (list) -> {
                StringBuilder sb = new StringBuilder();
                list.sort(SortingUtils.REQUIREMENT_COMPARATOR);
                list.forEach(req -> {
                    sb.append(renderRequirement(req));
                });
                return sb.toString();
            }),
            new Field<Catalogue, List<Milestone>>("milestones", Field.Type.LIST, Catalogue::getMilestones, (list) -> {
                StringBuilder sb = new StringBuilder();
                list.forEach(ms -> sb.append(renderMilestone(ms)));
                return sb.toString();
            }),
            new Field<Catalogue, Double>("sumTotal", Field.Type.NORMAL, Catalogue::getSum),
            new ParametrizedField<Catalogue, Double>("sumMS", Catalogue::getSum) {
                @Override
                public String renderCarefully(Catalogue instance, String parameter) {
                    return StringUtils.prettyPrint(instance.getSum(Integer.valueOf(parameter)));
                }
            },
            new ParametrizedField<Catalogue, Milestone>("milestoneName", null) {
                @Override
                public String renderCarefully(Catalogue instance, String parameter) {
                    Milestone ms = instance.getMilestoneByOrdinal(Integer.valueOf(parameter));
                    return ms != null ? ms.getName() : "";
                }
            }
    );
    private Group group = null;
    private Catalogue catalogue = null;
    /**
     * Existing:
     * milestone
     * .name
     * .date
     * .ordinal
     * .sumMax
     * .dateFormatted[<SimpleDateFormat>]
     */
    public final Entity<Milestone> MILESTONE_ENTITY = new Entity<Milestone>("milestone",
            new Field<Milestone, String>("name", Field.Type.NORMAL, Milestone::getName),
            new Field<Milestone, Date>("date", Field.Type.OBJECT, Milestone::getDate, date -> {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                return format.format(date);
            }),
            new ParametrizedField<Milestone, Date>("dateFormatted", Milestone::getDate) {
                private final Logger LOGGER = LogManager.getLogger(TemplateParser.class);

                @Override
                public String renderCarefully(Milestone instance, String parameter) {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat(parameter);
                        return format.format(getGetter().apply(instance));
                    } catch (IllegalArgumentException iae) {
                        LOGGER.error("The specified pattern is not compliant with java.text.SimpleDateFormat.", iae);
                    }
                    return "";
                }
            },
            new Field<Milestone, Integer>("ordinal", Field.Type.NORMAL, Milestone::getOrdinal),
            new Field<Milestone, Double>("sumMax", Field.Type.NORMAL, ms -> catalogue.getSum(ms.getOrdinal()))
    );

    /**
     * Existing:
     * progress
     * .points
     * .hasPoints
     * .isUnlocked[][]
     * .date
     * .dateFormatted[]
     * .milestone
     */
    public final Entity<Progress> PROGRESS_ENTITY = new Entity<Progress>("progress",
            Field.createNormalField("points", p -> p.getPointsSensitive(catalogue)),
            new ConditionalField<Progress>("hasPoints", Progress::hasProgress, b -> "POINTS EXISTING", b -> "NO POINTS"),
            new ConditionalField<Progress>("isUnlocked", p -> group.isProgressUnlocked(catalogue, p), b -> "UNLOCEKD", b -> "LOCKED"),
            new Field<Progress, Date>("date", Field.Type.OBJECT, Progress::getDate, date -> {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
                return format.format(date);
            }),
            new ParametrizedField<Progress, Date>("dateFormatted", Progress::getDate) {
                private final Logger LOGGER = LogManager.getLogger(TemplateParser.class);

                @Override
                public String renderCarefully(Progress instance, String parameter) {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat(parameter);
                        return format.format(getGetter().apply(instance));
                    } catch (IllegalArgumentException iae) {
                        LOGGER.error("The specified pattern is not compliant with java.text.SimpleDateFormat.", iae);
                    }
                    return "";
                }
            },
            new SubEntityField<Progress, Milestone>("minMS", (p -> catalogue.getMilestoneByOrdinal(p.getMilestoneOrdinal())), MILESTONE_ENTITY)
    );
    /**
     * Existing:
     * requirement
     * .name
     * .description
     * .maxPoints
     * .minMS
     * .predecessorNames
     * .binary[][]
     * .mandatory[][]
     * .malus[][]
     * .meta[<key>]
     * .singularMS[][]
     */
    public final Entity<Requirement> REQUIREMENT_ENTITY = new Entity<Requirement>("requirement",
            new Field<Requirement, String>("name", Field.Type.NORMAL, Requirement::getName),
            new Field<Requirement, String>("description", Field.Type.NORMAL, Requirement::getDescription),
            new Field<Requirement, Double>("maxPoints", Field.Type.NORMAL, Requirement::getMaxPointsSensitive),
            new SubEntityField<Requirement, Milestone>("minMS", (requirement -> {
                return catalogue.getMilestoneByOrdinal(requirement.getMinMilestoneOrdinal());
            }), MILESTONE_ENTITY),
            new SubEntityField<Requirement, Milestone>("maxMS", (requirement -> {
                return catalogue.getMilestoneByOrdinal(requirement.getMaxMilestoneOrdinal());
            }), MILESTONE_ENTITY),
            new Field<Requirement, List<String>>("predecessorNames", Field.Type.OBJECT, Requirement::getPredecessorNames, list -> {
                StringBuilder sb = new StringBuilder();
                list.forEach(str -> {
                    sb.append(str);
                    sb.append(", ");
                });
                String out = sb.toString();
                if (out.charAt(out.length() - 1) == ' ') {
                    return out.substring(0, out.lastIndexOf(", "));
                }
                return out;
            }),
            new ConditionalField<Requirement>("binary", Requirement::isBinary, b -> "BINARY", b -> "PARTIAL"),
            new ConditionalField<Requirement>("mandatory", Requirement::isMandatory, b -> "MANDATORY", b -> "BONUS"),
            new ConditionalField<Requirement>("malus", Requirement::isMalus, b -> "-", b -> "+"),
            new ParametrizedField<Requirement, Map<String, String>>("meta", Requirement::getPropertiesMap) {
                //private final Logger LOGGER = LogManager.getLogger(TemplateParser.class.getName().replace("TemplateParser", "MetaParser"));
                private final Logger LOGGER = LogManager.getLogger(TemplateParser.class);

                @Override
                public String renderCarefully(Requirement instance, String parameter) {
                    Map<String, String> map = getGetter().apply(instance);
                    if (!map.containsKey(parameter)) {
                        LOGGER.error(String.format("Error while parsing meta of requirement [name=%s]: There is no meta with name: %s", instance.getName(), parameter));
                        return "";
                    }
                    String value = map.get(parameter);
                    if (value != null) {
                        return value;
                    } else {
                        return "";
                    }
                }
            },
            new ConditionalField<Requirement>("singularMS", r -> r.getMinMilestoneOrdinal() == r.getMaxMilestoneOrdinal(), b-> "YES", b-> "NO")
    );


    /**
     * Existing:
     * groupMilestone
     * .name
     * .progressList
     * .sum
     * .percentage
     * .comment
     */
    public final Entity<Milestone> GROUP_MS_ENTITY = new Entity<Milestone>("groupMilestone",
            Field.createNormalField("name", Milestone::getName),
            new Field<Milestone, List<Progress>>("progressList", Field.Type.LIST, ms -> this.group.getProgressByMilestoneOrdinal(ms.getOrdinal()), list -> {
                StringBuilder sb = new StringBuilder();

                sortProgressList(list);


                list.forEach(p -> sb.append(renderProgress(p)));

                return sb.toString();
            }),
            new Field<Milestone, Double>("sum", Field.Type.NORMAL, (ms) -> group.getSumForMilestone(ms, catalogue)),
            new Field<Milestone, Double>("percentage", Field.Type.NORMAL, ms -> (group.getSumForMilestone(ms, catalogue) / catalogue.getSum(ms.getOrdinal())) * 100.0),
            new Field<Milestone, String>("comment", Field.Type.NORMAL, ms -> {
                ProgressSummary ps = group.getProgressSummaryForMilestone(ms);
                if (ps == null) {
                    return "";
                } else {
                    return ps.getExternalComment();
                }
            })
    );
    private Template<Milestone> templateGroupMSms = null;
    /**
     * Existing:
     * group
     * .name
     * .project
     * .milestones
     * .sumMS[]
     * .sumTotal
     */
    public final Entity<Group> GROUP_ENTITY = new Entity<Group>("group",
            Field.createNormalField("name", Group::getName),
            Field.createNormalField("project", Group::getProjectName),
            new Field<Group, List<Milestone>>("milestones", Field.Type.LIST, g -> g.getMilestonesForGroup(catalogue), list -> {
                StringBuilder sb = new StringBuilder();
                list.forEach(ms -> sb.append(renderGroupMilestone(ms)));
                return sb.toString();
            }),
            new Field<Group, Double>("sumTotal", Field.Type.NORMAL, (group1) -> group1.getTotalSum(catalogue)),
            new ParametrizedField<Group, Double>("sumMS", (group1) -> group1.getTotalSum(catalogue)) {

                @Override
                public String renderCarefully(Group instance, String parameter) {
                    Milestone ms = catalogue.getMilestoneByOrdinal(Integer.valueOf(parameter));
                    return StringUtils.prettyPrint(group.getSumForMilestone(ms, catalogue));
                }
            }

    );
    private Template<Catalogue> templateGroupCat = null;

    public RenderManager(Catalogue catalogue) {
        this.catalogue = catalogue;
        parser = new TemplateParser();
        renderer = new TemplateRenderer();
        LOGGER.debug("Catalogue: "+catalogue.getName() );
    }

    public RenderManager(Catalogue catalogue, Group group) {
        this(catalogue);
        this.group = group;
    }

    private static <E> String renderCarefully(TemplateRenderer renderer, Template<E> template, E instance) throws IllegalArgumentException, IllegalStateException {
        if (renderer == null) {
            throw new IllegalArgumentException("Cannot render if the passed TemplateRenderer is null");
        }
        if (instance == null) {
            throw new IllegalArgumentException("Cannot render an instance if the instance is null");
        }

        if (template != null) {
            return renderer.render(template, instance);
        } else {
            throw new IllegalStateException(String.format("Cannot render the instance [%s], if no template exists for.", instance));
        }
    }

    public boolean isGroupExportReady() {
        return group != null;
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

    public String renderMilestone(Milestone ms) {
        return renderCarefully(renderer, templateMS, ms);
    }

    public String renderRequirement(Requirement req) {
        return renderCarefully(renderer, templateReq, req);
    }

    public String renderCatalogue() {
        return renderCarefully(renderer, templateCat, catalogue);
    }

    public String renderGroupMilestone(Milestone ms) {
        String renderedGroupMS = renderCarefully(renderer, templateGroupMS, ms);
        parser.setupFor(MILESTONE_ENTITY);
        templateGroupMSms = parser.parseTemplate(renderedGroupMS);
        return renderCarefully(renderer, templateGroupMSms, ms);
    }

    public String renderProgress(Progress p) {
        String progressRendered = renderCarefully(renderer, templateProgress, p);
        Requirement r = catalogue.getRequirementForProgress(p);
        parser.setupFor(REQUIREMENT_ENTITY);
        Template<Requirement> templateProgressRequirement = parser.parseTemplate(progressRendered);
        return renderCarefully(renderer, templateProgressRequirement, r);
    }

    public void parseProgressTemplate(String template) {
        parseTemplateCarefully(PROGRESS_ENTITY, template);
    }

    public String renderGroup(Group g) {
        String groupRendered = renderCarefully(renderer, templateGroup, g);

        parser.setupFor(CATALOGUE_ENTITY);
        templateGroupCat = parser.parseTemplate(groupRendered);
        return renderCarefully(renderer, templateGroupCat, catalogue);
    }

    public void parseRequirementTemplate(String reqTemplate) {
        parseTemplateCarefully(REQUIREMENT_ENTITY, reqTemplate);
    }

    public void parseGroupMilestoneTemplate(String groupMSTemplate) {
        parseTemplateCarefully(GROUP_MS_ENTITY, groupMSTemplate);
    }

    public void parseGroupTemplate(String groupTemplate) {
        parseTemplateCarefully(GROUP_ENTITY, groupTemplate);
    }

    public void parseMilestoneTemplate(String msTemplate) {
        parseTemplateCarefully(MILESTONE_ENTITY, msTemplate);
    }

    public void parseCatalogueTemplate(String catTemplate) {
        parseTemplateCarefully(CATALOGUE_ENTITY, catTemplate);
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    private void sortProgressList(List<Progress> list) {
        list.sort(Comparator.comparing(catalogue::getRequirementForProgress, SortingUtils.REQUIREMENT_COMPARATOR));
    }

    private <E> boolean parseTemplateCarefully(Entity<E> entity, String template) {
        parser.setupFor(entity);
        if (MILESTONE_ENTITY.getEntityName().equals(entity.getEntityName())) {
            // Template MS
            templateMS = parser.parseTemplate(template);
        } else if (REQUIREMENT_ENTITY.getEntityName().equals(entity.getEntityName())) {
            // Template Req
            templateReq = parser.parseTemplate(template);
        } else if (CATALOGUE_ENTITY.getEntityName().equals(entity.getEntityName())) {
            // Template Cat
            templateCat = parser.parseTemplate(template);
        } else if (GROUP_MS_ENTITY.getEntityName().equals(entity.getEntityName())) {
            templateGroupMS = parser.parseTemplate(template);
        } else if (GROUP_ENTITY.getEntityName().equals(entity.getEntityName())) {
            templateGroup = parser.parseTemplate(template);
        } else if (PROGRESS_ENTITY.getEntityName().equals(entity.getEntityName())) {
            templateProgress = parser.parseTemplate(template);
        } else {
            throw LOGGER.throwing(new UnsupportedOperationException("Cannot parse a template for entity: " + entity.toString()));
        }
        return true;
    }
}
