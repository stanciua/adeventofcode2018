import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day20Test {

  @Test
  public void testDay6Result1() {
    try {
      Day20 day20 = new Day20();
      assertEquals(3502, day20.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }

  @Test
  public void testDay6Result2() {
    try {
      Day20 day20 = new Day20();
      assertEquals(-1, day20.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
