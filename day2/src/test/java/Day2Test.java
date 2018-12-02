import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Day2Test {

    @Test
    public void testDay1Result1() {
        try {
            Day2 day2 = new Day2("src/test/java/input.txt");
            assertEquals(5434, day2.getResult1());
        } catch(Exception e) {
            assert(false);
        }
    }
    @Test
    public void testDay1Result2() {
        try {
            Day2 day2 = new Day2("src/test/java/input.txt");
            assertEquals("agimdjvlhedpsyoqfzuknpjwt", day2.getResult2());
        } catch(Exception e) {
            assert(false);
        }
    }
}

