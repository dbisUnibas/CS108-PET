package ch.unibas.dmi.dbis.reqman.analysis;

import ch.unibas.dmi.dbis.reqman.data.Catalogue;
import ch.unibas.dmi.dbis.reqman.data.Course;
import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Member;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Precision;
import org.apache.logging.log4j.core.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class which exports a list of groups as csv
 *
 * @author loris.sauter
 */
public class CSVOverviewGroupExporter {
  
  private final Catalogue catalogue;
  private final Course course;
  private final CatalogueAnalyser catalogueAnalyser;
  private ArrayList<Group> groups= new ArrayList<>();
  
  private String gradeFormula;
  
  
  
  private CSVOverviewGroupExporter(Catalogue catalogue, Course course, Group...groups) {
    this(catalogue,course,Arrays.asList(groups));
  }
  
  
  private CSVOverviewGroupExporter(Catalogue catalogue, Course course, List<Group> groups) {
    this.catalogue = catalogue;
    this.course = course;
    this.catalogueAnalyser = new CatalogueAnalyser(course, catalogue);
    this.groups.addAll(groups);
  }
  
  private boolean hasGradeForumla(){
    return StringUtils.isNotBlank(gradeFormula);
  }
  
  private static final String LF = "\n";
  private static final String SEPARATOR = ",";
  
  public static CSVOverviewGroupExporter createOverviewExporter(Catalogue catalogue, Course course, Group...groups){
    return new CSVOverviewGroupExporter(catalogue,course,groups);
  }
  
  public static CSVOverviewGroupExporter createOverviewExporter(Catalogue catalogue, Course course, List<Group> groups){
    return new CSVOverviewGroupExporter(catalogue,course,groups);
  }
  
  public static CSVOverviewGroupExporter createGradedOverviewExporter(String gradeFormula, Catalogue catalogue, Course course, Group...groups){
    CSVOverviewGroupExporter exporter = new CSVOverviewGroupExporter(catalogue,course,groups);
    exporter.gradeFormula = gradeFormula;
    return exporter;
  }
  
  public static CSVOverviewGroupExporter createGradedOverviewExporter(String gradeFormula, Catalogue catalogue, Course course, List<Group> groups){
    CSVOverviewGroupExporter exporter = new CSVOverviewGroupExporter(catalogue,course,groups);
    exporter.gradeFormula = gradeFormula;
    return exporter;
  }
  
  /**
   * Creates a long overview table: milestones as columns and for each group a row
   *
   * Implementation is also row based
   *
   * @return
   */
  private String buildLongOverviewTable(){
    final StringBuilder sb = new StringBuilder();
    
    final double maxSum = catalogueAnalyser.getMaximalRegularSum();
    
    // First Row: Headers: Group,MS1,...,MSi,...,MSn,Total[,Grade]
    sb.append("Group");
    sb.append(SEPARATOR);
    catalogue.getMilestones().forEach(ms -> {
      sb.append(ms.getName());
      sb.append(SEPARATOR);
    });
    sb.append("Total");
    
    if(hasGradeForumla()){
      sb.append(SEPARATOR);
      sb.append("Grade");
    }
    sb.append(LF);
    // End of first row
    
    // Rows: per group
    groups.forEach(group -> {
      GroupAnalyser groupAnalyser = new GroupAnalyser(course,catalogue,group);
      // Column 0: Group name
      sb.append(group.getName());
      
      // Columns 1 to n (for each milestone)
      catalogue.getMilestones().forEach(ms -> {
        sb.append(SEPARATOR);
        sb.append(ch.unibas.dmi.dbis.reqman.common.StringUtils.prettyPrint(groupAnalyser.getSumFor(groupAnalyser.getProgressSummaryFor(ms))));
      });
      
      // [Almost] Last column: Total
      sb.append(SEPARATOR);
      sb.append(ch.unibas.dmi.dbis.reqman.common.StringUtils.prettyPrint(groupAnalyser.getSum()));
      
      if(hasGradeForumla()){
        // Last column, if grade formula present: Grade
        sb.append(SEPARATOR);
        double points = groupAnalyser.getSum();
        double grade = new ExpressionBuilder(gradeFormula).variables("p", "max").build().setVariable("p", points).setVariable("max", maxSum).evaluate();
        grade = Precision.round(grade, 2);
        sb.append(ch.unibas.dmi.dbis.reqman.common.StringUtils.prettyPrint(grade));
      }
      
      sb.append(LF);
    });
  
    return sb.toString();
  }
  
  /**
   * Creates a long overview table: milestones as columns and for each group member a row
   *
   * Implementation is also row based
   *
   * @return
   */
  private String buildLongOverviewTablePerMember(){
    final StringBuilder sb = new StringBuilder();
    
    final double maxSum = catalogueAnalyser.getMaximalRegularSum();
    
    // First Row: Headers: Member,Group,MS1,...,MSi,...,MSn,Total[,Grade]
    sb.append("Member");
    sb.append(SEPARATOR);
    sb.append("Group");
    sb.append(SEPARATOR);
    catalogue.getMilestones().forEach(ms -> {
      sb.append(ms.getName());
      sb.append(SEPARATOR);
    });
    sb.append("Total");
    
    if(hasGradeForumla()){
      sb.append(SEPARATOR);
      sb.append("Grade");
    }
    sb.append(LF);
    // End of first row
    
    // Rows: per group
    groups.forEach(group -> {
      GroupAnalyser groupAnalyser = new GroupAnalyser(course,catalogue,group);
      
      for(Member m : group.getMembers()){
  
        // Column 0: Member Name
        StringBuilder memberBuilder = new StringBuilder();
        memberBuilder.append(m.getFirstName());
        memberBuilder.append(" ");
        memberBuilder.append(m.getName());
        
        sb.append(memberBuilder.toString().trim());
        
  
        // Columns 1 to n (for each milestone)
        catalogue.getMilestones().forEach(ms -> {
          sb.append(SEPARATOR);
          sb.append(ch.unibas.dmi.dbis.reqman.common.StringUtils.prettyPrint(groupAnalyser.getSumFor(groupAnalyser.getProgressSummaryFor(ms))));
        });
  
        // [Almost] Last column: Total
        sb.append(SEPARATOR);
        sb.append(ch.unibas.dmi.dbis.reqman.common.StringUtils.prettyPrint(groupAnalyser.getSum()));
  
        if(hasGradeForumla()){
          // Last column, if grade formula present: Grade
          sb.append(SEPARATOR);
          double points = groupAnalyser.getSum();
          double grade = new ExpressionBuilder(gradeFormula).variables("p", "max").build().setVariable("p", points).setVariable("max", maxSum).evaluate();
          grade = Precision.round(grade, 2);
          sb.append(ch.unibas.dmi.dbis.reqman.common.StringUtils.prettyPrint(grade));
        }
  
        sb.append(LF);
      }
      
    });
    
    return sb.toString();
  }
  
  public void exportLongOverviewTable(File file) throws IOException {
    if(StringUtils.isBlank(FileUtils.getFileExtension(file))){
      file = new File(file.getPath()+".csv");
    }
    BufferedWriter br = new BufferedWriter(new FileWriter(file));
    br.write(buildLongOverviewTable());
    br.flush();
    br.close();
  }
  
  public void exportLongMemberOverviewTable(File file) throws IOException {
    if(StringUtils.isBlank(FileUtils.getFileExtension(file))){
      file = new File(file.getPath()+".csv");
    }
    BufferedWriter br = new BufferedWriter(new FileWriter(file));
    br.write(buildLongOverviewTablePerMember());
    br.flush();
    br.close();
  }
  
  
  
}
