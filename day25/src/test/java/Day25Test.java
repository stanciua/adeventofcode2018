import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day25Test {

  @Test
  public void testDay6Result1() {
    try {
      Day25 day25 = new Day25();
      assertEquals(314, day25.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }
}
