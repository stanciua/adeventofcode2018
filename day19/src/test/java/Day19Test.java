import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day19Test {

  @Test
  public void testDay6Result1() {
    try {
      Day19 day19 = new Day19();
      assertEquals(1302, day19.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }

  @Test
  public void testDay6Result2() {
    try {
      Day19 day19 = new Day19();
      assertEquals(594, day19.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
