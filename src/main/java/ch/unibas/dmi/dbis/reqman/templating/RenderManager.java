package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.common.SortingUtils;
import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.*;
import ch.unibas.dmi.dbis.reqman.management.OverviewSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO: write JavaDoc
 *
 * @author loris.sauter
 */
public class RenderManager {
  private static Logger LOGGER = LogManager.getLogger(RenderManager.class);
  
  /**
   * Existent:
   * overview
   * .minTotal
   * .avgTotal
   * .maxTotla
   * .minMS[]
   * .avgMS[]
   * .maxMS[]
   * .sumTotal[]
   * .sumMS[ms=,group=]
   * .minTotalGroups
   * .avgTotalGroups
   * .maxTotalGroups
   * .minMSGroups[]
   * .avgMSGroups[]
   * .maxMSGroups[]
   * .nameMS[]
   * .name
   */
  private final Entity<OverviewSnapshot> OVERVIEW_ENTITY = new Entity<OverviewSnapshot>("overview",
      new Field<OverviewSnapshot, Double>("minTotal", Field.Type.NORMAL, OverviewSnapshot::getTotalMin),
      new Field<OverviewSnapshot, Double>("avgTotal", Field.Type.NORMAL, OverviewSnapshot::getTotalAvg),
      new Field<OverviewSnapshot, Double>("maxTotal", Field.Type.NORMAL, OverviewSnapshot::getTotalMax),
      new Field<OverviewSnapshot, List<Group>>("minTotalGroups", Field.Type.LIST, OverviewSnapshot::getTotalMinList, RenderManager::renderGroupNameList),
      new Field<OverviewSnapshot, List<Group>>("maxTotalGroups", Field.Type.LIST, OverviewSnapshot::getTotalMaxList, RenderManager::renderGroupNameList),
      new ParametrizedField<OverviewSnapshot, Double>("minMS", null) {
        @Override
        public String renderCarefully(OverviewSnapshot instance, String parameter) {
          return StringUtils.roundTo2Digits(instance.getMsMin(Integer.valueOf(parameter)));
        }
      },
      new ParametrizedField<OverviewSnapshot, Double>("avgMS", null) {
        @Override
        public String renderCarefully(OverviewSnapshot instance, String parameter) {
          return StringUtils.roundTo2Digits(instance.getMsAvg(Integer.valueOf(parameter)));
        }
      },
      new ParametrizedField<OverviewSnapshot, Double>("maxMS", null) {
        @Override
        public String renderCarefully(OverviewSnapshot instance, String parameter) {
          return StringUtils.roundTo2Digits(instance.getMsMax(Integer.valueOf(parameter)));
        }
      },
      new ParametrizedField<OverviewSnapshot, List<Group>>("minMSGroups", null) {
        @Override
        public String renderCarefully(OverviewSnapshot instance, String parameter) {
          return RenderManager.renderGroupNameList(instance.getMsMinList(Integer.valueOf(parameter)));
        }
      },
      new ParametrizedField<OverviewSnapshot, List<Group>>("maxMSGroups", null) {
        @Override
        public String renderCarefully(OverviewSnapshot instance, String parameter) {
          return RenderManager.renderGroupNameList(instance.getMsMaxList(Integer.valueOf(parameter)));
        }
      },
      new ParametrizedField<OverviewSnapshot, String>("sumTotal", null) {
        @Override
        public String renderCarefully(OverviewSnapshot instance, String parameter) {
          return StringUtils.roundTo2Digits(instance.getTotalSum(instance.getGroup(parameter)));
        }
      },
      new ParametrizedField<OverviewSnapshot, String>("sumMS", null) {
        @Override
        public String renderCarefully(OverviewSnapshot instance, String parameter) {
          if (!parameter.contains(",")) {
            return "[INVALID FORMAT]";
          }
          String sub1 = parameter.substring(0, parameter.indexOf(","));
          String sb2 = parameter.substring(parameter.indexOf(",") + 1);
          if (sub1.startsWith("ms=") && sb2.startsWith("group=")) {
            int ord = Integer.valueOf(sub1.substring(sub1.indexOf("=") + 1));
            String gr = sb2.substring(sb2.indexOf("=") + 1);
            return StringUtils.roundTo2Digits(instance.getMsSum(instance.getGroup(gr), ord));
          } else if (sub1.startsWith("group=") && sb2.startsWith("ms=")) {
            int ord = Integer.valueOf(sb2.substring(sb2.indexOf("=") + 1));
            String gr = sub1.substring(sub1.indexOf("=") + 1);
            return StringUtils.roundTo2Digits(instance.getMsSum(instance.getGroup(gr), ord));
          } else {
            return "[INVALID OPTIONS]";
          }
        }
      },
      new ParametrizedField<OverviewSnapshot, String>("nameMS", null) {
        @Override
        public String renderCarefully(OverviewSnapshot instance, String parameter) {
          return instance.getMilestoneName(Integer.valueOf(parameter));
        }
      },
      new Field<OverviewSnapshot, String>("name", Field.Type.NORMAL, OverviewSnapshot::getCatalogueName)
  );
  private Template<Requirement> templateReq = null;
  private Template<Milestone> templateMS = null;
  private Template<Catalogue> templateCat = null;
  private Template<Milestone> templateGroupMS = null;
  private Template<Group> templateGroup = null;
  private Template<Progress> templateProgress = null;
  private Template<OverviewSnapshot> templateOverview = null;
  private TemplateParser parser = new TemplateParser();
  private TemplateRenderer renderer = new TemplateRenderer();
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
   * .bonusTotal
   * .malusTotal
   * .bonusMS[<ordinal>]
   * .malusMS[<ordinal>]
   * <p>
   *
   */
  private final Entity<Catalogue> CATALOGUE_ENTITY = new Entity<Catalogue>("catalogue",
      new Field<Catalogue, String>("name", Field.Type.NORMAL, Catalogue::getName),
      new Field<Catalogue, String>("description", Field.Type.NORMAL, Catalogue::getDescription),
      new Field<Catalogue, String>("lecture", Field.Type.NORMAL, c -> EntityController.getInstance().getCourse().getName()),
      new Field<Catalogue, String>("semester", Field.Type.NORMAL, c -> EntityController.getInstance().getCourse().getSemester()),
      new Field<Catalogue, List<Requirement>>("requirements", Field.Type.LIST, Catalogue::getRequirements, (list) -> {
        StringBuilder sb = new StringBuilder();
        list.sort(EntityController.getInstance().getCatalogueAnalyser().getRequirementComparator());
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
      new Field<Catalogue, Double>("sumTotal", Field.Type.NORMAL, c -> EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSum()),
      new Field<Catalogue, Double>("bonusTotal", Field.Type.NORMAL, c-> EntityController.getInstance().getCatalogueAnalyser().getMaximalBonusSum()),
      new Field<Catalogue,Double>("malusTotal", Field.Type.NORMAL,c->EntityController.getInstance().getCatalogueAnalyser().getMaximalMalusSum()),
      new ParametrizedField<Catalogue, Double>("sumMS", _unused -> 0d) {
        @Override
        public String renderCarefully(Catalogue instance, String parameter) {
          Milestone ms = EntityController.getInstance().getCatalogueAnalyser().getMilestoneByPosition(Integer.valueOf(parameter));
          if (ms == null) {
            return "[ERROR: No such Milestone " + parameter + "]";
          } else {
            return StringUtils.prettyPrint(EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSumFor(ms));
          }
        }
      },
      new ParametrizedField<Catalogue, Double>("bonusMS", _unused -> 0d) {
        @Override
        public String renderCarefully(Catalogue instance, String parameter) {
          Milestone ms = EntityController.getInstance().getCatalogueAnalyser().getMilestoneByPosition(Integer.valueOf(parameter));
          if (ms == null) {
            return "[ERROR: No such Milestone " + parameter + "]";
          } else {
            return StringUtils.prettyPrint(EntityController.getInstance().getCatalogueAnalyser().getMaximalBonusSumFor(ms));
          }
        }
      },
      new ParametrizedField<Catalogue, Double>("malusMS", _unused -> 0d) {
        @Override
        public String renderCarefully(Catalogue instance, String parameter) {
          Milestone ms = EntityController.getInstance().getCatalogueAnalyser().getMilestoneByPosition(Integer.valueOf(parameter));
          if (ms == null) {
            return "[ERROR: No such Milestone " + parameter + "]";
          } else {
            return StringUtils.prettyPrint(EntityController.getInstance().getCatalogueAnalyser().getMaximalMalusSumFor(ms));
          }
        }
      },
      new ParametrizedField<Catalogue, Milestone>("milestoneName", null) {
        @Override
        public String renderCarefully(Catalogue instance, String parameter) {
          Milestone ms = EntityController.getInstance().getCatalogueAnalyser().getMilestoneByPosition(Integer.valueOf(parameter));
          if (ms == null) {
            return "[ERROR: No such Milestone " + parameter + "]";
          } else {
            return ms.getName();
          }
        }
      }
  );
  /**
   * Existing:
   * milestone
   * .name
   * .date
   * .ordinal
   * .sumMax
   * .dateFormatted[<SimpleDateFormat>]
   * .requirements
   * .bonusMax
   * .malusMax
   */
  public final Entity<Milestone> MILESTONE_ENTITY = new Entity<Milestone>("milestone",
      new Field<Milestone, String>("name", Field.Type.NORMAL, Milestone::getName),
      new Field<Milestone, Date>("date", Field.Type.OBJECT, ms -> EntityController.getInstance().getCourseManager().getMilestoneDate(ms), date -> {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
        return format.format(date);
      }),
      new ParametrizedField<Milestone, Date>("dateFormatted", ms -> EntityController.getInstance().getCourseManager().getMilestoneDate(ms)) {
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
      new Field<Milestone, Integer>("ordinal", Field.Type.NORMAL, ms -> EntityController.getInstance().getCourseManager().getMilestoneOrdinal(ms)),
      new Field<Milestone, Double>("sumMax", Field.Type.NORMAL, ms -> EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSumFor(ms)),
      new Field<Milestone, Double>("bonusMax", Field.Type.NORMAL, ms -> EntityController.getInstance().getCatalogueAnalyser().getMaximalBonusSumFor(ms)),
      new Field<Milestone, Double>("malusMax", Field.Type.NORMAL, ms -> EntityController.getInstance().getCatalogueAnalyser().getMaximalMalusSumFor(ms)),
      new Field<Milestone, List<Requirement>>("requirements", Field.Type.LIST, ms -> EntityController.getInstance().getCatalogueAnalyser().getRequirementsFor(ms), (list) -> {
        StringBuilder sb = new StringBuilder();
        list.sort(EntityController.getInstance().getCatalogueAnalyser().getRequirementComparator());
        list.forEach(req -> {
          sb.append(renderRequirement(req));
        });
        return sb.toString();
      })
  );
  /**
   * Existing:
   * requirement
   * .name
   * .excerpt
   * .maxPoints
   * .minMS
   * .predecessorNames
   * .binary[][]
   * .mandatory[][]
   * .malus[][]
   * .meta[<key>]
   * .singularMS[][]
   * .type
   * .category
   * .description
   */
  public final Entity<Requirement> REQUIREMENT_ENTITY = new Entity<Requirement>("requirement",
      new Field<Requirement, String>("name", Field.Type.NORMAL, Requirement::getName),
      new Field<Requirement, String>("excerpt", Field.Type.NORMAL, Requirement::getExcerpt),
      new Field<Requirement, String>("type", Field.Type.NORMAL, r -> r.getType().toString()),
      new Field<Requirement, String>("category", Field.Type.NORMAL, Requirement::getCategory),
      new Field<Requirement, Double>("maxPoints", Field.Type.NORMAL, Requirement::getMaxPoints),
      new SubEntityField<Requirement, Milestone>("minMS", (requirement -> {
        return EntityController.getInstance().getCourseManager().getMinimalMilestone(requirement);
      }), MILESTONE_ENTITY),
      new SubEntityField<Requirement, Milestone>("maxMS", (requirement -> {
        return EntityController.getInstance().getCourseManager().getMaximalMilestone(requirement);
      }), MILESTONE_ENTITY),
      new Field<Requirement, List<String>>("predecessorNames", Field.Type.OBJECT, r -> EntityController.getInstance().getCatalogueAnalyser().getPredecessors(r).stream().map(Requirement::getName).collect(Collectors.toList()), list -> {
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
        //private final Logger LOGGER = LogManager.getLogger(TemplateParser.class.getCatalogueName().replace("TemplateParser", "MetaParser"));
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
      new ConditionalField<Requirement>("singularMS", r -> r.getMinimalMilestoneUUID().equals(r.getMaximalMilestoneUUID()), b -> "YES", b -> "NO")
  );
  private Group group = null;
  private Catalogue catalogue = null;
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
      new SubEntityField<Progress, Milestone>("milestone", (p -> catalogue.getMilestoneByOrdinal(p.getMilestoneOrdinal())), MILESTONE_ENTITY)
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
    LOGGER.debug("Catalogue: " + catalogue.getName());
  }
  
  public RenderManager(Catalogue catalogue, Group group) {
    this(catalogue);
    this.group = group;
  }
  
  public RenderManager(OverviewSnapshot overview) {
  
  }
  
  private static final String renderGroupNameList(List<Group> list) {
    StringBuilder sb = new StringBuilder();
    list.forEach(g -> {
      sb.append(g.getName());
      sb.append(", ");
    });
    sb.deleteCharAt(sb.length() - 1); // remove last spcae
    sb.deleteCharAt(sb.length() - 1); // remove last comma
    return sb.toString();
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
  
  public String renderOverview(OverviewSnapshot overview) {
    return renderCarefully(renderer, templateOverview, overview);
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
  
  public void parseOverviewTemplate(String template) {
    parseTemplateCarefully(OVERVIEW_ENTITY, template);
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
    } else if (OVERVIEW_ENTITY.getEntityName().equals(entity.getEntityName())) {
      templateOverview = parser.parseTemplate(template);
    } else {
      throw LOGGER.throwing(new UnsupportedOperationException("Cannot parse a template for entity: " + entity.toString()));
    }
    return true;
  }
}
