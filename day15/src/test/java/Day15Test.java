import org.javatuples.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day15Test {

  @Test
  public void testDay6Result1() {
    try {
      Day15 day15 = new Day15();
      assertEquals(-1, day15.getResult1());
    } catch (Exception e) {
      assert (false);
    }
  }

  // @Test
  // public void testDay6Result2() {
  //   try {
  //     Day15 day15 = new Day15();
  //     assertEquals(-1, day15.getResult2());
  //   } catch (Exception e) {
  //     assert (false);
  //   }
  // }
}
