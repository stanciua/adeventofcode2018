import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day17Test {

  @Test
  public void testDay6Result1() {
    try {
      Day17 day17 = new Day17();
      assertEquals(521, day17.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }

  @Test
  public void testDay6Result2() {
    try {
      Day17 day17 = new Day17();
      assertEquals(594, day17.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
