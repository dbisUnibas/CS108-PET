package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class RenderManager {
    private static final Logger LOG = LogManager.getLogger(RenderManager.class);
    private static Logger LOGGER = LogManager.getLogger(RenderManager.class);
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
            new Field<Milestone, Integer>("ordinal", Field.Type.NORMAL, Milestone::getOrdinal)
    );
    /**
     * Existing:
     * progress
     * .points
     * .hasPoints
     */
    public final Entity<Progress> PROGRESS_ENTITY = new Entity<Progress>("progress",
            Field.createNormalField("points", Progress::getPoints),
            new ConditionalField<Progress>("hasPoints", this::hasProgressPoints, b -> "POINTS EXISTING", b -> "NO POINTS")
    );
    private Template<Requirement> templateReq = null;
    private Template<Milestone> templateMS = null;
    private Template<Catalogue> templateCat = null;
    private Template<Milestone> templateGroupMS = null;
    private Template<Group> templateGroup = null;
    private Template<Progress> templateProg = null;
    private Template<Requirement> templateProgReq = null;
    private TemplateParser parser = null;
    /*
    Target expressions:

    progress.points
    progressSummary.internal
    progressSummary.external

    group.name
    group.project
    group.progressList // Warning: Ordering must be respected (MS, BONUS; MALUS, NAME)
        access to milestone and requirement
    group.milestones
        access to milestone entity

    NOT SOLVED YET:
        when to apply 'achieved'

     */
    private TemplateRenderer renderer = null;
    public final Entity<Catalogue> CATALOGUE_ENTITY = new Entity<Catalogue>("catalogue",
            new Field<Catalogue, String>("name", Field.Type.NORMAL, Catalogue::getName),
            new Field<Catalogue, String>("description", Field.Type.NORMAL, Catalogue::getDescription),
            new Field<Catalogue, String>("lecture", Field.Type.NORMAL, Catalogue::getLecture),
            new Field<Catalogue, String>("semester", Field.Type.NORMAL, Catalogue::getSemester),
            new Field<Catalogue, List<Requirement>>("requirements", Field.Type.LIST, Catalogue::getRequirements, (list) -> {
                StringBuilder sb = new StringBuilder();
                list.sort(Comparator.comparingInt(Requirement::getMinMilestoneOrdinal)
                        .thenComparing(Requirement::isMandatory, (b1, b2) -> {
                            if (b1 == b2) {
                                return 0;
                            } else if (b1) {
                                return -1;
                            } else {
                                return 1;
                            }
                        })

                        .thenComparing(Requirement::isMalus, (b1, b2) -> {
                            if (b1 == b2) {
                                return 0;
                            } else if (b1) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }).thenComparing(Requirement::getName));
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
                    return String.valueOf(instance.getSum(Integer.valueOf(parameter)));
                }
            },
            new ParametrizedField<Catalogue, Milestone>("milestoneName", null) {
                @Override
                public String renderCarefully(Catalogue instance, String parameter) {
                    Milestone ms = instance.getMilestoneByOrdinal(Integer.valueOf(parameter));
                    return ms != null ? ms.getName() : "null (" + parameter + ")";
                }
            }
    );
    private Catalogue catalogue = null;
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
            }
    );
    /**
     * Existing:
     * group
     * .name
     * .project
     * .milestones
     */
    public final Entity<Group> GROUP_ENTITY = new Entity<Group>("group",
            Field.createNormalField("name", Group::getName),
            Field.createNormalField("project", Group::getProjectName),
            new Field<Group, List<Milestone>>("milestones", Field.Type.LIST, this::getMilestonesForGroup, list -> {
                StringBuilder sb = new StringBuilder();
                list.forEach(ms -> sb.append(renderGroupMilestone(ms)));
                return sb.toString();
            })
    );
    private Group group = null;
    /**
     * Existing:
     * groupMilestone
     * .name
     * .progressList
     * .sum
     */
    public final Entity<Milestone> GROUP_MS_ENTITY = new Entity<Milestone>("groupMilestone",
            Field.createNormalField("name", Milestone::getName),
            new Field<Milestone, List<Progress>>("progressList", Field.Type.LIST, ms -> this.getProgressByMilestoneOrdinal(ms.getOrdinal()), list -> {
                StringBuilder sb = new StringBuilder();

                sortProgressList(list);


                list.forEach(p -> sb.append(renderProgress(p)));

                return sb.toString();
            }),
            new Field<Milestone, Double>("sum", Field.Type.NORMAL, this::getSumForGroupMilestone)
    );

    public RenderManager(Catalogue catalogue) {
        this.catalogue = catalogue;
        parser = new TemplateParser(catalogue);
        renderer = new TemplateRenderer();
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

    private boolean hasProgressPoints(Progress progress) {
        return Double.compare(progress.getPoints(), 0d) != 0;
    }

    private void sortProgressList(List<Progress> list) {
        list.sort(Comparator.comparing(this::getRequirementForProgress, Comparator.comparing(Requirement::isMandatory, (b1, b2) -> {
            if (b1 == b2) {
                return 0;
            } else if (b1) {
                return -1;
            } else {
                return 1;
            }
        }).thenComparing(Requirement::isMalus, (b1, b2) -> {
            if (b1 == b2) {
                return 0;
            } else if (b1) {
                return 1;
            } else {
                return -1;
            }
        }).thenComparing(Requirement::getName)));
    }

    private Requirement getRequirementForProgress(Progress progress) {
        return catalogue.getRequirementByName(progress.getRequirementName());
    }

    private Milestone getMilestoneForProgress(Progress progress) {
        return catalogue.getMilestoneByOrdinal(progress.getMilestoneOrdinal());
    }

    private List<Milestone> getMilestonesForGroup(Group group) {
        ArrayList<Milestone> list = new ArrayList<>();

        for (Progress p : group.getProgressList()) {
            Milestone ms = getMilestoneForProgress(p);
            if (!list.contains(ms)) {
                list.add(ms);
            } else {
                // Milestone already in list.
            }
        }

        return list;
    }

    private double getSumForGroupMilestone(Milestone ms) {
        ArrayList<Double> points = new ArrayList<>();

        getProgressByMilestoneOrdinal(ms.getOrdinal()).forEach(p -> {
            Requirement req = getRequirementForProgress(p);
            double factor = req.isMalus() ? -1.0 : 1.0;
            points.add(factor * p.getPoints());
        });

        return points.stream().mapToDouble(Double::doubleValue).sum();
    }

    private List<Progress> getProgressByMilestoneOrdinal(int ordinal) {
        ArrayList<Progress> list = new ArrayList<>();
        for (Progress p : group.getProgressList()) {
            if (p.getMilestoneOrdinal() == ordinal) {
                if (!list.contains(p)) {
                    list.add(p);
                } else {
                    // Progress already in list.
                }
            }
        }
        return list;
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
        return renderCarefully(renderer, templateGroupMS, ms);
    }

    public String renderProgress(Progress p) {
        String progressRendered = renderCarefully(renderer, templateProg, p);
        Requirement r = getRequirementForProgress(p);
        parser.setupFor(REQUIREMENT_ENTITY);
        templateProgReq = parser.parseTemplate(progressRendered);
        return renderCarefully(renderer, templateProgReq, r);
    }

    public void parseProgressTemplate(String template) {
        parseTemplateCarefully(PROGRESS_ENTITY, template);
    }


    public String renderGroup(Group g) {
        return renderCarefully(renderer, templateGroup, g);
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
            templateProg = parser.parseTemplate(template);
        } else {
            throw LOGGER.throwing(new UnsupportedOperationException("Cannot parse a template for entity: " + entity.toString()));
        }
        return true;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
