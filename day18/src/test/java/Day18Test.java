import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day18Test {

  @Test
  public void testDay6Result1() {
    try {
      Day18 day18 = new Day18();
      assertEquals(663502, day18.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }

  @Test
  public void testDay6Result2() {
    try {
      Day18 day18 = new Day18();
      assertEquals(594, day18.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
