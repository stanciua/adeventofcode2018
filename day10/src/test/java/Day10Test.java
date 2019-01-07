import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day10Test {

    @Test
    public void testDay6Result1() {
        try {
            Day10 day10 = new Day10();
            assertEquals("BITRAQVSGUWKXYHMZPOCDLJNFE", day10.getResult1());
        } catch(Exception e) {
            assert(false);
        }
    }
    @Test
    public void testDay6Result2() {
        try {
            Day10 day10 = new Day10();
            assertEquals(869, day10.getResult2());
        } catch(Exception e) {
            assert(false);
        }
    }
}

