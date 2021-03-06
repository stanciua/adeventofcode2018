import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day5Test {

    @Test
    public void testDay4Result1() {
        try {
            Day5 day5 = new Day5();
            assertEquals(9296, day5.getResult1());
        } catch(Exception e) {
            assert(false);
        }
    }
    @Test
    public void testDay4Result2() {
        try {
            Day5 day5 = new Day5();
            assertEquals(5534, day5.getResult2());
        } catch(Exception e) {
            assert(false);
        }
    }
}

