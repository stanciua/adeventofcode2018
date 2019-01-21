import org.javatuples.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day11Test {

  @Test
  public void testDay6Result1() {
    Day11 day11 = new Day11();
    assertEquals(new Pair<>(new Pair<>(34, 13), 3), day11.getResult1());
  }

  @Test
  public void testDay6Result2() {
    Day11 day11 = new Day11();
    assertEquals(new Pair<>(new Pair<>(280, 218), 11), day11.getResult2());
  }
}
