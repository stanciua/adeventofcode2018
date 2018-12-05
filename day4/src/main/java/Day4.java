import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day4 {
    private Record[] records;

    Day4() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/java/input.txt")));
        records = input.lines().map(Record::new).sorted((r1, r2) -> r1.time.compareTo(r2.time)).toArray(Record[]::new);
        updateId();
        removeAllNotLazyGuards();
    }

    void removeAllNotLazyGuards() {
        Set<Integer> sleepingIds = Arrays.stream(records).filter(r -> r.state == State.FALLS_ASLEEP).map(r -> r.id).collect(Collectors.toCollection(HashSet::new));
        Set<Integer> toBeRemovedIds = Arrays.stream(records).map(r -> r.id).collect(Collectors.toCollection(HashSet::new));
        toBeRemovedIds.removeAll(sleepingIds);
        records  = Arrays.stream(records).filter(r -> !toBeRemovedIds.contains(r.id)).toArray(Record[]::new);
    }
    void updateId() {
        int currentId = records[0].id;
        for (Record record: records) {
            if (record.id == 0) {
                record.id = currentId;
            } else {
                currentId = record.id;
            }
        }
    }

    int[] getSleepTimeById(int id) {
        LocalDateTime[] asleepTime = Arrays.stream(records).filter(r -> r.id == id && r.state == State.FALLS_ASLEEP).map(r -> r.time).toArray(LocalDateTime[]::new);
        LocalDateTime[] wakeUpTime = Arrays.stream(records).filter(r -> r.id == id && r.state == State.WAKES_UP).map(r -> r.time).toArray(LocalDateTime[]::new);
        return IntStream.range(0, asleepTime.length).map(i -> Math.abs(wakeUpTime[i].getMinute() - asleepTime[i].getMinute())).toArray();
    }

    int getMostSleepingId() {
        int mostSleepingId = 0;
        int maxSum = 0;

        for (Record record: records) {
            int sum = Arrays.stream(getSleepTimeById(record.id)).sum();
//            System.out.print(sum + " " + Arrays.stream(getSleepTimeById(record.id)).mapToObj(i -> String.valueOf(i)).reduce("", (acc, x) -> acc + x + " "));
            if (sum > maxSum) {
                mostSleepingId = record.id;
                maxSum = sum;
            }
        }
        return mostSleepingId;
    }

    public int getResult1() {
        int mostSleepingId = getMostSleepingId();
        OptionalInt longestSleep = Arrays.stream(getSleepTimeById(mostSleepingId)).max();
        if (longestSleep.isPresent()) {
            System.out.println(longestSleep.getAsInt());
            return (longestSleep.getAsInt() - 1) * mostSleepingId;
        }
        return 0;
    }

    public int getIdWhoSleepsTheMost() {
        int maxSum = 0;
        int idByMax = 0;
        for (int i : Arrays.stream(records).map(r -> r.id).collect(Collectors.toCollection(HashSet::new))) {
//            System.out.println("Id: " + i);
            int sum = Arrays.stream(getMinutesOfSleep(i)).sum();
//            System.out.println(sum);
//            int sum = Arrays.stream(getMinutesOfSleep(i)).peek(r -> System.out.print(r + " ")).sum();
//            System.out.println();
            if (sum > maxSum) {
                idByMax = i;
                maxSum = sum;
            }
        }

        return idByMax;
    }

    public int getResult2() {
        return 0;
    }
}

class Record {
    LocalDateTime time;
    int id;
    State state;
    final static Pattern beginShift = Pattern.compile("\\[(\\d+)\\-(\\d+)\\-(\\d+)\\s*(\\d+):(\\d+)\\]\\s*Guard\\s*#(\\d+)\\s*begins shift");
    final static Pattern fallsAsleep = Pattern.compile("\\[(\\d+)\\-(\\d+)\\-(\\d+)\\s*(\\d+):(\\d+)\\]\\s*falls asleep");
    final static Pattern wakesUp = Pattern.compile("\\[(\\d+)\\-(\\d+)\\-(\\d+)\\s*(\\d+):(\\d+)\\]\\s*wakes up");

    Record(String record) {
        Matcher match = beginShift.matcher(record);
        if (match.matches()) {
            time = LocalDateTime.of(
                    Integer.valueOf(match.group(1)),
                    Integer.valueOf(match.group(2)),
                    Integer.valueOf(match.group(3)),
                    Integer.valueOf(match.group(4)),
                    Integer.valueOf(match.group(5)));
            id = Integer.valueOf(match.group(6));
            state = State.BEGINS_SHFIT;
        }
        match = fallsAsleep.matcher(record);
        if (match.matches()) {
            time = LocalDateTime.of(
                    Integer.valueOf(match.group(1)),
                    Integer.valueOf(match.group(2)),
                    Integer.valueOf(match.group(3)),
                    Integer.valueOf(match.group(4)),
                    Integer.valueOf(match.group(5)));
            state = State.FALLS_ASLEEP;
        }

        match = wakesUp.matcher(record);
        if (match.matches()) {
            time = LocalDateTime.of(
                    Integer.valueOf(match.group(1)),
                    Integer.valueOf(match.group(2)),
                    Integer.valueOf(match.group(3)),
                    Integer.valueOf(match.group(4)),
                    Integer.valueOf(match.group(5)));
            state = State.WAKES_UP;
    }

    public String toString() {
        return id + "\n" + time + "\n" + state + "\n";
    }
}

enum State {
    BEGINS_SHIFT,
    WAKES_UP,
    FALLS_ASLEEP
}
