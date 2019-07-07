import org.javatuples.Triplet;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
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
  public void testOctreeImplementation() {
    var boxMin = new Triplet<Long, Long, Long>(0L, 2L, 6L);
    var boxMax = new Triplet<Long, Long, Long>(5L, 10L, 9L);
    var box = new Day23.Box(boxMin, boxMax);
    var octree = new Day23.Octree(box);
    var five = octree.add(5, new Triplet<>(3L, 4L, 8L));
    octree.add(8, new Triplet<>(3L, 4L, 9L));
    octree.add(10, new Triplet<>(3L, 4L, 9L));
    octree.add(7, new Triplet<>(2L, 5L, 8L));
    octree.add(2, new Triplet<>(1L, 6L, 7L));
    
    var count = octree.getElementsAtPoint(new Triplet<>(3, 4, 9));
    System.out.println(count);
    octree.remove(8);
    var count2 = octree.getElementsAtPoint(new Triplet<>(3L, 4L, 8L));
    System.out.println(count2);
    
    var boxMin2 = new Triplet<>(1L, 3L, 7L);
    var boxMax2 = new Triplet<>(4L, 9L, 8L);
    var box2 = new Day23.Box(boxMin2, boxMax2);
    
    assertTrue(box.isContainedInBox(box2));
    assertTrue(box.intersectsBox(box2));

    var boxMin3 = new Triplet<>(3L, 8L, 8L);
    var boxMax3 = new Triplet<>(10L, 12L, 20L);
    var box3 = new Day23.Box(boxMin3, boxMax3);

    assertTrue(box3.intersectsBox(box3));
    
    System.out.println(octree.getElementInBox(box));
    System.out.println(octree.getElementInBox(box2));
    
  }
  
  @Test
  public void testDay6Result2() {
    try {
      Day23 day23 = new Day23();
      assertEquals(594, day23.getResult2());
    } catch (Exception e) {
      assert (false);
    }
  }
}
