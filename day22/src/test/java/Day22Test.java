import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day22Test {

  @Test
  public void testDay6Result1() {
    try {
      Day22 day22 = new Day22();
      assertEquals(11575, day22.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }

  @Test
  public void testDay6Result2() {
    try {
      Day22 day22 = new Day22();
      assertEquals(1068, day22.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
