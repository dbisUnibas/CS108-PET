package ch.unibas.dmi.dbis.reqman.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TODO: Write JavaDoc
 *
 * @author loris.sauter
 */
public class VersionTest {
  
  
  @Test
  public void testParseVersionNormal() {
    String vStr = "1.2.3";
    Version v = Version.forString(vStr);
    assertVersion(vStr, v);
    assertEquals(vStr, v.getVersion());
  }
  
  @Test
  public void testParseVersionFull() {
    String vStr = "1.2.3-alpha.3";
    Version v = Version.forString(vStr);
    assertVersion(vStr, v);
    assertEquals("1.2.3", v.getVersion());
  }
  
  @Test
  public void testParseInvalidVersion() {
    String vStr = "1.2";
    Version v = Version.forString(vStr);
    assertTrue(v.isInvalid());
  }
  
  @Test
  public void testCompareNormal() {
    String vs1 = "1.2.3";
    String vs2 = "4.5.6";
    Version v1 = Version.forString(vs1);
    Version v2 = Version.forString(vs2);
    assertTrue(0 > v1.compareTo(v2));
    assertTrue(0 < v2.compareTo(v1));
  }
  
  @Test
  public void testCompareMinor() {
    String vs1 = "1.2.3";
    String vs2 = "1.4.5";
    Version v1 = Version.forString(vs1);
    Version v2 = Version.forString(vs2);
    assertTrue(0 > v1.compareTo(v2));
    assertTrue(0 < v2.compareTo(v1));
  }
  
  @Test
  public void testComparePatch() {
    String vs1 = "0.1.2";
    String vs2 = "0.1.3";
    Version v1 = Version.forString(vs1);
    Version v2 = Version.forString(vs2);
    assertTrue(0 > v1.compareTo(v2));
    assertTrue(0 < v2.compareTo(v1));
  }
  
  @Test
  public void testCompareFull() {
    String vs1 = "0.1.2-alpha.1";
    String vs2 = "0.3.4.alpha.2";
    Version v1 = Version.forString(vs1);
    Version v2 = Version.forString(vs2);
    assertTrue(0 > v1.compareTo(v2));
    assertTrue(0 < v2.compareTo(v1));
  }
  
  @Test
  public void testCompareEqual() {
    String vs1 = "0.1.2";
    String vs2 = "0.1.2";
    Version v1 = Version.forString(vs1);
    Version v2 = Version.forString(vs2);
    assertTrue(0 == v1.compareTo(v2));
    assertTrue(0 == v2.compareTo(v1));
  }
  
  @Test
  public void testCompareEqualFull() {
    String vs1 = "0.1.2-PR1";
    String vs2 = "0.1.2-PR1";
    Version v1 = Version.forString(vs1);
    Version v2 = Version.forString(vs2);
    assertTrue(0 == v1.compareTo(v2));
    assertTrue(0 == v2.compareTo(v1));
  }
  
  private void assertVersion(String vStr, Version v) {
    assertEquals(vStr, v.getFullVersion());
    assertEquals(1, v.getMajor());
    assertEquals(2, v.getMinor());
    assertEquals(3, v.getPatch());
  }
}