import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day3Test {

    @Test
    public void testDay1Result1() {
        try {
            Day3 day3 = new Day3("src/test/java/input.txt");
            assertEquals(100595, day3.getResult1());
        } catch(Exception e) {
            assert(false);
        }
    }
    @Test
    public void testDay1Result2() {
        try {
            Day3 day3 = new Day3("src/test/java/input.txt");
            assertEquals(100595, day3.getResult1());
            assertEquals(415, day3.getResult2());
        } catch(Exception e) {
            assert(false);
        }
    }
}

