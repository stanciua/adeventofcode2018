import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Day4 {
    private Record[] records;

    Day4() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/java/input.txt")));
        records = input.lines().map(Record::new).sorted((r1, r2) -> r1.time.compareTo(r2.time)).toArray(Record[]::new);
        updateRecordId();
        // remove records that don't have any sleep
        Set<Integer> asleepIds = Arrays.stream(records).filter(r -> r.state == State.FALLS_ASLEEP).map(r -> r.id).collect(Collectors.toCollection(HashSet::new));
        Set<Integer> toBeRemoved = Arrays.stream(records).map(r -> r.id).collect(Collectors.toCollection(HashSet::new));
        toBeRemoved.removeAll(asleepIds);
        records = Arrays.stream(records).filter(r -> !toBeRemoved.contains(r.id)).toArray(Record[]::new);
//        Arrays.stream(records).forEach(System.out::println);
    }

    void updateRecordId() {
        // the first record will always have an id of non-zero
        int currentId = records[0].id;
        for (Record record : records) {
            if (record.id == 0) {
                record.id = currentId;
            } else {
                currentId = record.id;
            }
        }
    }

    int[] getMinutesOfSleep(int id) {
        LocalDateTime[] fallsAsleepTimes = Stream.of(records).filter(r -> r.id == id && r.state == State.FALLS_ASLEEP).map(r -> r.time).toArray(LocalDateTime[]::new);
        LocalDateTime[] wakesUptimes = Stream.of(records).filter(r -> r.id == id && r.state == State.WAKES_UP).map(r -> r.time).toArray(LocalDateTime[]::new);
        return IntStream.range(0, fallsAsleepTimes.length).map(i -> Math.abs(fallsAsleepTimes[i].getMinute() - wakesUptimes[i].getMinute())).toArray();
    }

    public int getResult1() {
        int idMostSleep = getIdWhoSleepsTheMost();
//        System.out.println(idMostSleep);
//        OptionalInt maxSleepTime = Arrays.stream(getMinutesOfSleep(idMostSleep)).peek(e -> System.out.print(e + " ")).max();
        OptionalInt maxSleepTime = Arrays.stream(getMinutesOfSleep(idMostSleep)).max();
        if (maxSleepTime.isPresent()) {
//            System.out.println(maxSleepTime.getAsInt());
            return (maxSleepTime.getAsInt() - 1) * idMostSleep;
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

    public String toString() {
        return "time: " + time + "\n" + "id: " + id + "\n" + "state: " + state + "\n";
    }

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
    }
}

enum State {
    BEGINS_SHFIT,
    WAKES_UP,
    FALLS_ASLEEP
}
