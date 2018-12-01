import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day1Test {

    @Test
    public void testDay1Result1() {
        try {
            Day1 day1 = new Day1("src/test/java/input.txt");
            assertEquals(402, day1.getResult1());
        } catch(Exception e) {
            assert(false);
        }
    }
    @Test
    public void testDay1Result2() {
        try {
            Day1 day1 = new Day1("src/test/java/input.txt");
            assertEquals(481, day1.getResult2());
        } catch(Exception e) {
            assert(false);
        }
    }
}

