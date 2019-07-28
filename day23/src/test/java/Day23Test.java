import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day23Test {

  @Test
  public void testDay6Result1() {
    try {
      Day23 day23 = new Day23();
      assertEquals(253L, day23.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }
  
  @Test
  public void testDay6Result2() {
    try {
      Day23 day23 = new Day23();
      assertEquals(108618801L, day23.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
