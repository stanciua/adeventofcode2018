import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day7Test {

    @Test
    public void testDay6Result1() {
        try {
            Day7 day7 = new Day7();
            assertEquals("BITRAQVSGUWKXYHMZPOCDLJNFE", day7.getResult1());
        } catch(Exception e) {
            assert(false);
        }
    }
    @Test
    public void testDay6Result2() {
        try {
            Day7 day7 = new Day7();
            assertEquals(45602, day7.getResult2());
        } catch(Exception e) {
            assert(false);
        }
    }
}

