import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day6Test {

    @Test
    public void testDay6Result1() {
        try {
            Day6 day6 = new Day6();
            assertEquals(3290, day6.getResult1());
        } catch(Exception e) {
            assert(false);
        }
    }
    @Test
    public void testDay6Result2() {
        try {
            Day6 day6 = new Day6();
            assertEquals(45602, day6.getResult2());
        } catch(Exception e) {
            assert(false);
        }
    }
}

