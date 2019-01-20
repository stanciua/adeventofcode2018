import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day11Test {

  @Test
  public void testDay6Result1() {
    try {
      Day11 day11 = new Day11();
      assertEquals(-1, day11.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }

  @Test
  public void testDay6Result2() {
    try {
      Day11 day11 = new Day11();
      assertEquals(-1, day11.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
