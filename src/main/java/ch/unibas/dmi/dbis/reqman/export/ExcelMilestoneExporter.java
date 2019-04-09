package ch.unibas.dmi.dbis.reqman.export;

import ch.unibas.dmi.dbis.reqman.data.Group;
import ch.unibas.dmi.dbis.reqman.data.Requirement;
import ch.unibas.dmi.dbis.reqman.data.Requirement.Type;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exports all achievements for a given milestone to an excel-file which is easier to use for grading on-the-fly during
 * presentations. Given more time, it would potentially be nicer to add a seperate UI view for ReqMan but why reinvent
 * the wheel
 *
 * @author silvan.heller
 */
public class ExcelMilestoneExporter {
  
  private static XSSFCellStyle groupNameStyle;
  private static XSSFCellStyle headerStyle;
  private static XSSFCellStyle footerStyle;
  
  private static XSSFWorkbook _workbook;
  private static Group _group;
  private static XSSFSheet _sheet;
  private static List<Requirement> _reqs;
  private static int _rowNum;
  private static Map<Type, Pair<XSSFCellStyle, XSSFCellStyle>> _styles = new HashMap<>();
  
  public static void exportRequirements(List<Requirement> reqs, List<Group> groups, File export) {
    _reqs = reqs;
    _rowNum = 0;
    _workbook = new XSSFWorkbook();
    
    Font boldFont = _workbook.createFont();
    boldFont.setBold(true);
    
    groupNameStyle = _workbook.createCellStyle();
    groupNameStyle.setFont(boldFont);
    
    headerStyle = _workbook.createCellStyle();
    headerStyle.setFont(boldFont);
    headerStyle.setBottomBorderColor(new XSSFColor(Color.BLACK));
    headerStyle.setBorderBottom(BorderStyle.THIN);
    
    footerStyle = _workbook.createCellStyle();
    footerStyle.setFont(boldFont);
    footerStyle.setTopBorderColor(new XSSFColor(IndexedColors.BLACK, new DefaultIndexedColorMap()));
    footerStyle.setBorderTop(BorderStyle.THIN);
    
    XSSFCellStyle one = generateStyle(IndexedColors.WHITE);
    XSSFCellStyle two = generateStyle(IndexedColors.GREY_25_PERCENT);
    _styles.put(Type.REGULAR, Pair.create(one, two));
    
    one = generateStyle(IndexedColors.LIGHT_GREEN);
    two = generateStyle(IndexedColors.LIGHT_TURQUOISE);
    _styles.put(Type.BONUS, Pair.create(one, two));
    
    one = generateStyle(IndexedColors.ROSE);
    two = generateStyle(IndexedColors.TAN);
    _styles.put(Type.MALUS, Pair.create(one, two));
    
    for (Group group : groups) {
      _group = group;
      _sheet = _workbook.createSheet(group.getName());
      writeReqs();
      XSSFRow row = _sheet.createRow(_rowNum++);
      row.createCell(0).setCellStyle(footerStyle);
      row.createCell(1).setCellStyle(footerStyle);
      row.createCell(2).setCellStyle(footerStyle);
      XSSFCell cell = row.createCell(3);
      cell.setCellValue("Total");
      cell.setCellStyle(footerStyle);
      
      cell = row.createCell(4, CellType.FORMULA);
      cell.setCellFormula("SUMIF(A3:A23, \"YES\", E3:E" + (2 + reqs.size()) + ")");
      cell.setCellStyle(footerStyle);
    }
    
    try {
      FileOutputStream outputStream = new FileOutputStream(export);
      _workbook.write(outputStream);
      _workbook.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private static void writeRow(IndexedColors col) {
    XSSFRow row = _sheet.createRow(_rowNum++);
    row.createCell(0).setCellStyle(generateStyle(col));
    row.createCell(1).setCellValue(col.index);
  }
  
  
  private static XSSFCellStyle generateStyle(IndexedColors color) {
    XSSFCellStyle style = _workbook.createCellStyle();
    
    style.setFillForegroundColor(new XSSFColor(color, new DefaultIndexedColorMap()));
    style.setWrapText(true);
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return style;
  }
  
  private static void writeReqs() {
    //Write Header
    _rowNum = 0;
    Row row = _sheet.createRow(_rowNum++);
    int colNum = 0;
    Cell cell = row.createCell(colNum++, CellType.STRING);
    cell.setCellValue(prettyPrintGroupName(_group));
    cell.setCellStyle(groupNameStyle);
    colNum = 0;
    String[] headers = {"Achieved", "Achievement", "Description", "Comment", "Points"};
    row = _sheet.createRow(_rowNum++);
    for (String header : headers) {
      cell = row.createCell(colNum++, CellType.STRING);
      cell.setCellValue(header);
      cell.setCellStyle(headerStyle);
    }

    /*
      Write requirements
     */
    DataValidation dataValidation = null;
    DataValidationConstraint constraint = null;
    DataValidationHelper validationHelper = null;
    validationHelper = new XSSFDataValidationHelper(_sheet);
    CellRangeAddressList addressList = new CellRangeAddressList(2, 2 + _reqs.size(), 0, 0);
    constraint = validationHelper.createExplicitListConstraint(new String[]{"Yes", "No"});
    dataValidation = validationHelper.createValidation(constraint, addressList);
    _sheet.addValidationData(dataValidation);
    
    writeReqs(Type.REGULAR);
    writeReqs(Type.MALUS);
    writeReqs(Type.BONUS);
    
    _sheet.setColumnWidth(0, ("Achieved".length() + 2) * 256);
    _sheet.autoSizeColumn(1);
    _sheet.setColumnWidth(2, 60 * 256);
    _sheet.setColumnWidth(3, 50 * 256);
    _sheet.autoSizeColumn(4);
  }
  
  private static void writeReqs(Type type) {
    //Write Regular requirements
    boolean one = true;
    for (Requirement req : _reqs) {
      if (req.getType() == type) {
        int colNum = 0;
        XSSFRow row = _sheet.createRow(_rowNum++);
        generateCell(row, colNum++, CellType.STRING, one ? _styles.get(type).getFirst() : _styles.get(type).getSecond());
        generateCell(row, colNum++, CellType.STRING, one ? _styles.get(type).getFirst() : _styles.get(type).getSecond()).setCellValue(req.getName());
        generateCell(row, colNum++, CellType.STRING, one ? _styles.get(type).getFirst() : _styles.get(type).getSecond()).setCellValue(req.getExcerpt());
        generateCell(row, colNum++, CellType.STRING, one ? _styles.get(type).getFirst() : _styles.get(type).getSecond()).setCellValue("");
        generateCell(row, colNum++, CellType.NUMERIC, one ? _styles.get(type).getFirst() : _styles.get(type).getSecond()).setCellValue(type != Type.MALUS ? req.getMaxPoints() : -req.getMaxPoints());
        one = !one;
      }
    }
  }
  
  private static Cell generateCell(Row row, int colNum, CellType string, XSSFCellStyle style) {
    Cell cell = row.createCell(colNum);
    cell.setCellStyle(style);
    return cell;
  }
  
  
  private static String prettyPrintGroupName(Group group) {
    return group.getName() + (group.getProjectName() == null ? "" : (" - " + group.getProjectName()));
  }
  
}
