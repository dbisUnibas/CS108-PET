package ch.unibas.dmi.dbis.reqman.templating;

import ch.unibas.dmi.dbis.reqman.common.StringUtils;
import ch.unibas.dmi.dbis.reqman.control.EntityController;
import ch.unibas.dmi.dbis.reqman.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
  
  private Template<Requirement> templateReq = null;
  private Template<Milestone> templateMS = null;
  private Template<Catalogue> templateCat = null;
  private Template<ProgressSummary> templateProgressSummary = null;
  private Template<Group> templateGroup = null;
  private Template<Progress> templateProgress = null;
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
            return "[ERROR: No such Milestone " + parameter + "]"; // TODO Introduce --debug mode for template: if errors, print them, otherwise be quiet
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
      new Field<Requirement, String>("type", Field.Type.NORMAL, r -> r.getType().toString().toLowerCase()),
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
   * progressSummary
   * .name
   * .progressList
   * .sum
   * .percentage
   * .comment
   * .summary --> {Progress.name: Progress.comment\n}
   */
  public final Entity<ProgressSummary> PROGRESS_SUMMARY_ENTITY = new Entity<ProgressSummary>("progressSummary",
      new Field<ProgressSummary, String>("name", Field.Type.NORMAL, ps -> EntityController.getInstance().getCatalogueAnalyser().getMilestoneOf(ps).getName()),
      new Field<ProgressSummary, List<Progress>>("progressList", Field.Type.LIST, ps -> EntityController.getInstance().getGroup(group.getUuid()).getProgressList(), list -> {
        StringBuilder sb = new StringBuilder();
        
        list.sort((p1,p2)-> {
          Requirement r1 = EntityController.getInstance().getCatalogueAnalyser().getRequirementOf(p1);
          Requirement r2 = EntityController.getInstance().getCatalogueAnalyser().getRequirementOf(p2);
          return EntityController.getInstance().getCatalogueAnalyser().getRequirementComparator().compare(r1,r2);
        });
        
        list.forEach(p -> sb.append(renderProgress(p)));
        
        return sb.toString();
      }),
      new Field<ProgressSummary, Double>("sum", Field.Type.NORMAL, ps -> EntityController.getInstance().getGroupAnalyser(group).getSumFor(ps)),
      new Field<ProgressSummary, Double>("percentage", Field.Type.NORMAL, ps -> (EntityController.getInstance().getGroupAnalyser(group).getSumFor(ps) / EntityController.getInstance().getCatalogueAnalyser().getMaximalRegularSumFor(ps)) * 100.0),
      new Field<ProgressSummary, String>("comment", Field.Type.NORMAL, ps -> {
        if (ps == null) {
          return "";
        } else {
          return ps.getExternalComment();
        }
      }),
      new Field<ProgressSummary, List<String>>("summary", Field.Type.LIST, ps -> {
        List<String> list = new ArrayList<>();
        EntityController.getInstance().getGroupAnalyser(group).getProgressFor(ps).forEach(p -> {
          list.add(EntityController.getInstance().getCatalogueAnalyser().getRequirementOf(p).getName()+": "+p.getComment()+"\n"); // TODO Line spearator as parameter / config / ?
        });
        return list;
      }, list -> {
        StringBuilder sb = new StringBuilder();
        list.forEach(str -> sb.append(str));
        return sb.toString();
      })
  );
  
  /**
   * Existing:
   * progress
   * .points
   * .fraction
   * .hasPoints
   * .isUnlocked[][]
   * .date
   * .dateFormatted[]
   * .milestone
   * .comment
   * .progressSummary
   */
  public final Entity<Progress> PROGRESS_ENTITY = new Entity<Progress>("progress",
      Field.createNormalField("points", p -> EntityController.getInstance().getCatalogueAnalyser().getActualPoints(p)),
      Field.createNormalField("comment",Progress::getComment),
      Field.createNormalField("fraction", Progress::getFraction),
      new ConditionalField<Progress>("hasPoints", Progress::hasProgress, b -> "POINTS EXISTING", b -> "NO POINTS"),
      new ConditionalField<Progress>("isUnlocked", p -> EntityController.getInstance().getGroupAnalyser(group).isProgressUnlocked(p), b -> "UNLOCEKD", b -> "LOCKED"),
      new Field<Progress, Date>("date", Field.Type.OBJECT, Progress::getAssessmentDate, date -> {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY");
        return format.format(date);
      }),
      new ParametrizedField<Progress, Date>("dateFormatted", Progress::getAssessmentDate) {
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
      new SubEntityField<Progress, Milestone>("milestone", (p -> {
        // Issue: No milestone associated, due to not assessed?--> check this!
        return EntityController.getInstance().getGroupAnalyser(group).getMilestoneOf(p);
      }), MILESTONE_ENTITY),
      new SubEntityField<Progress, ProgressSummary>("progressSummary", (p-> EntityController.getInstance().getGroupAnalyser(group).getProgressSummaryOf(p)), PROGRESS_SUMMARY_ENTITY)
  );
  private Template<ProgressSummary> templateProgressSummaryMilestone = null;
  /**
   * Existing:
   * group
   * .name
   * .project
   * .progressSummaries
   * .progressList //TODO
   * .sumMS[]
   * .sumTotal
   * // TODO print members?
   */
  public final Entity<Group> GROUP_ENTITY = new Entity<Group>("group",
      Field.createNormalField("name", Group::getName),
      Field.createNormalField("project", Group::getProjectName),
      new Field<Group, List<ProgressSummary>>("progressSummaries", Field.Type.LIST, g -> EntityController.getInstance().getGroup(g.getUuid()).getProgressSummaries(), list -> {
        StringBuilder sb = new StringBuilder();
        list.forEach(ms -> sb.append(renderProgressSummary(ms)));
        return sb.toString();
      }),
      new Field<Group, Double>("sumTotal", Field.Type.NORMAL, g -> EntityController.getInstance().getGroupAnalyser(g).getSum()),
      new ParametrizedField<Group, Double>("sumMS", g -> 0d) {
        
        @Override
        public String renderCarefully(Group instance, String parameter) {
          Milestone ms = EntityController.getInstance().getCatalogueAnalyser().getMilestoneByPosition(Integer.parseInt(parameter));
          if(ms == null){
            return "[ERROR] Milestone ("+parameter+") not found.";
          }
          ProgressSummary ps = EntityController.getInstance().getGroupAnalyser(group).getProgressSummaryFor(ms);
          return StringUtils.prettyPrint(EntityController.getInstance().getGroupAnalyser(group).getSumFor(ps));
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
  
  public String renderProgressSummary(ProgressSummary ps) {
    String renderedProgressSummary = renderCarefully(renderer, templateProgressSummary, ps);
    parser.setupFor(PROGRESS_SUMMARY_ENTITY);
    templateProgressSummaryMilestone = parser.parseTemplate(renderedProgressSummary);
    return renderCarefully(renderer, templateProgressSummaryMilestone, ps);
  }
  
  public String renderProgress(Progress p) {
    String progressRendered = renderCarefully(renderer, templateProgress, p);
    Requirement r = EntityController.getInstance().getCatalogueAnalyser().getRequirementOf(p);
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
  
  public void parseProgressSummaryTemplate(String progressSummaryTemplate) {
    parseTemplateCarefully(PROGRESS_SUMMARY_ENTITY, progressSummaryTemplate);
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
    } else if (PROGRESS_SUMMARY_ENTITY.getEntityName().equals(entity.getEntityName())) {
      templateProgressSummary = parser.parseTemplate(template);
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
