import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day9Test {

    @Test
    public void testDay6Result1() {
        try {
            Day9 day9 = new Day9(465, 71940);
            assertEquals( 384475, day9.getResult1());
        } catch(Exception e) {
            assert(false);
        }
    }
    @Test
    public void testDay6Result2() {
        try {
            Day9 day9 = new Day9(465, 7194000);
            assertEquals(-1, day9.getResult1());
        } catch(Exception e) {
            assert(false);
        }
    }
}

