import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day21Test {

  @Test
  public void testDay6Result1() {
    try {
      Day21 day21 = new Day21();
      assertEquals(13522479, day21.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }

  @Test
  public void testDay6Result2() {
    try {
      Day21 day21 = new Day21();
      assertEquals(14626276, day21.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
