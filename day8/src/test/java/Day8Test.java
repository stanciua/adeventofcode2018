import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day8Test {

    @Test
    public void testDay6Result1() {
        try {
            Day8 day8 = new Day8();
            assertEquals( 43996, day8.getResult1());
        } catch(Exception e) {
            assert(false);
        }
    }
    @Test
    public void testDay6Result2() {
        try {
            Day8 day8 = new Day8();
            assertEquals(35189, day8.getResult2());
        } catch(Exception e) {
            assert(false);
        }
    }
}

