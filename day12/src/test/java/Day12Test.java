import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day12Test {

  @Test
  public void testDay6Result1() {
    try {
      Day12 day12 = new Day12();
      assertEquals(1991, day12.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }

  @Test
  public void testDay6Result2() {
    try {
      Day12 day12 = new Day12();
      assertEquals(1100000000511L, day12.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
