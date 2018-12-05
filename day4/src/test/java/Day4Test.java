import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day4Test {

    @Test
    public void testDay4Result1() {
        try {
            Day4 day4 = new Day4();
            assertEquals(38813, day4.getResult1());
        } catch(Exception e) {
            assert(false);
        }
    }
    @Test
    public void testDay4Result2() {
        try {
            Day4 day4 = new Day4();
            assertEquals(141071, day4.getResult2());
        } catch(Exception e) {
            assert(false);
        }
    }
}

