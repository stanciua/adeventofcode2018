import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day13Test {

  @Test
  public void testDay6Result1() {
    try {
      Day13 day13 = new Day13();
      assertEquals(-1, day13.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }

  @Test
  public void testDay6Result2() {
    try {
      Day13 day13 = new Day13();
      assertEquals(-1, day13.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
