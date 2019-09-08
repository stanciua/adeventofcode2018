import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day24Test {

  @Test
  public void testDay6Result1() {
    try {
      Day24 day24 = new Day24();
      assertEquals(14377, day24.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }
  
  @Test
  public void testDay6Result2() {
    try {
      Day24 day24 = new Day24();
      assertEquals(6947, day24.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
