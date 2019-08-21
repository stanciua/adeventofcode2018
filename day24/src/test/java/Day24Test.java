import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day24Test {

  @Test
  public void testDay6Result1() {
    try {
      Day24 day24 = new Day24();
      assertEquals(253L, day24.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }
  
  @Test
  public void testDay6Result2() {
    try {
      Day24 day24 = new Day24();
      assertEquals(108618801L, day24.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
