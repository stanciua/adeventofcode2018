import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

class Day4 {
    private Record[] records;
    private int[][] area;

    Day4() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/java/input.txt")));
        records = input.lines().map(Record::new).toArray(Record[]::new);
    }

    public int getResult1() {
        return 0;
    }
    public int getResult2() {
        return 0;
    }
}

class Record {
    LocalDateTime time;
    int id;
    State state;
    final static Pattern beginShift = Pattern.compile("[(\\d+)\\-(\\d+)\\-(\\d+)\\s*(\\d+):(\\d+)]\\s*Guard\\s*#(\\d+)\\s*begins shift");
    final static Pattern fallsAsleep = Pattern.compile("[(\\d+)\\-(\\d+)\\-(\\d+)\\s*(\\d+):(\\d+)]\\s*falls asleep");
    final static Pattern wakesUp = Pattern.compile("[(\\d+)\\-(\\d+)\\-(\\d+)\\s*(\\d+):(\\d+)]\\s*wakes up");

    Record(String record) {
        var match = beginShift.matcher(record);
        if (match.matches()) {
            time = LocalDateTime.of(match.group(1), )
        }
    }
}

enum State {
    WAKES_UP,
    FALLS_ASLEEP
}
