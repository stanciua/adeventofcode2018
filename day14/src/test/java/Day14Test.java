import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day14Test {

  @Test
  public void testDay6Result1() {
    try {
      Day14 day14 = new Day14();
      assertEquals("3718110721", day14.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }

  @Test
  public void testDay6Result2() {
    try {
      Day14 day14 = new Day14();
      assertEquals(20298300, day14.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
