import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day4 {

  private Record[] records;

  Day4() throws Exception {
    String input = new String(Files.readAllBytes(Paths.get("src/test/java/input.txt")));
    records =
        input.lines().map(Record::new).sorted(Comparator.comparing(Record::getTime))
            .toArray(Record[]::new);
    updateId();
  }

  private void updateId() {
    int currentId = records[0].getId();
    for (Record record : records) {
      if (record.getId() == 0) {
        record.setId(currentId);
      } else {
        currentId = record.getId();
      }
    }
  }

  private int[] getSleepTimeById(int id) {
    LocalDateTime[] asleepTime =
        Arrays.stream(records).filter(r -> r.getId() == id && r.getState() == State.FALLS_ASLEEP)
            .map(Record::getTime).toArray(LocalDateTime[]::new);
    LocalDateTime[] wakeUpTime =
        Arrays.stream(records).filter(r -> r.getId() == id && r.getState() == State.WAKES_UP)
            .map(Record::getTime).toArray(LocalDateTime[]::new);
    return IntStream.range(0, asleepTime.length)
        .map(i -> wakeUpTime[i].getMinute() - asleepTime[i].getMinute()).toArray();
  }

  private int getMostSleepingId() {
    int mostSleepingId = 0;
    int maxSum = 0;
    for (int id :
        Arrays.stream(records).map(Record::getId).collect(Collectors.toCollection(HashSet::new))) {
      int sum = Arrays.stream(getSleepTimeById(id)).sum();
      if (sum > maxSum) {
        mostSleepingId = id;
        maxSum = sum;
      }
    }
    return mostSleepingId;
  }


  private int[] countMinutesSleptById(int id) {
    int[] minutes = new int[60];
    int[] asleepTime =
        Arrays.stream(records).filter(r -> r.getId() == id && r.getState() == State.FALLS_ASLEEP)
            .mapToInt(r -> r.getTime().getMinute()).toArray();
    int[] wakeUpTime =
        Arrays.stream(records).filter(r -> r.getId() == id && r.getState() == State.WAKES_UP)
            .mapToInt(r -> r.getTime().getMinute()).toArray();
    for (int i = 0; i < asleepTime.length; i++) {
      for (int from = asleepTime[i]; from < wakeUpTime[i]; from++) {
        minutes[from]++;
      }
    }
    return minutes;
  }

  private int getMostSleptMinute(int[] minutes) {
    OptionalInt maxMinuteCount = Arrays.stream(minutes).max();
    if (maxMinuteCount.isPresent()) {
      OptionalInt maxMinute =
          IntStream.range(0, minutes.length).filter(i -> minutes[i] == maxMinuteCount.getAsInt())
              .findFirst();
      if (maxMinute.isPresent()) {
        return maxMinute.getAsInt();
      }
    }

    return -1;
  }

  private int getMostSleptMinuteCount(int[] minutes) {
    OptionalInt mostSleptMinute = Arrays.stream(minutes).max();
    if (mostSleptMinute.isPresent()) {
      return mostSleptMinute.getAsInt();
    }

    return -1;
  }

  int getResult1() {
    int mostSleepingId = getMostSleepingId();
    int[] minutes = countMinutesSleptById(mostSleepingId);
    return mostSleepingId * getMostSleptMinute(minutes);
  }

  int getResult2() {
    int idWithMaxCount = 0;
    int countMax = 0;
    for (int id :
        Arrays.stream(records).map(Record::getId).collect(Collectors.toCollection(HashSet::new))) {
      int[] minutes = countMinutesSleptById(id);
      int count = getMostSleptMinuteCount(minutes);
      if (count > countMax) {
        countMax = count;
        idWithMaxCount = id;
      }
    }

    int[] minutes = countMinutesSleptById(idWithMaxCount);
    return getMostSleptMinute(minutes) * idWithMaxCount;

  }
}


