import java.time.LocalDateTime;
import java.util.regex.Pattern;

class Record {

  private LocalDateTime time;
  private int id;
  private State state;
  private static final Pattern beginShift = Pattern.compile("\\[(\\d+)-(\\d+)-(\\d+)\\s*(\\d+):"
      + "(\\d+)]\\s*Guard\\s*#(\\d+)\\s*begins shift");
  private static final Pattern fallsAsleep = Pattern.compile("\\[(\\d+)-(\\d+)-(\\d+)\\s*(\\d+):"
      + "(\\d+)]\\s*falls asleep");
  private static final Pattern wakesUp = Pattern.compile("\\[(\\d+)-(\\d+)-(\\d+)\\s*(\\d+):"
      + "(\\d+)]\\s*wakes up");

  Record(String record) {
    var match = beginShift.matcher(record);
    if (match.matches()) {
      setTime(LocalDateTime.of(Integer.valueOf(match.group(1)), Integer.valueOf(match.group(2)),
          Integer.valueOf(match.group(3)), Integer.valueOf(match.group(4)),
          Integer.valueOf(match.group(5))));
      setId(Integer.valueOf(match.group(6)));
      setState(State.BEGINS_SHIFT);
    }

    match = fallsAsleep.matcher(record);
    if (match.matches()) {
      setTime(LocalDateTime.of(Integer.valueOf(match.group(1)), Integer.valueOf(match.group(2)),
          Integer.valueOf(match.group(3)), Integer.valueOf(match.group(4)),
          Integer.valueOf(match.group(5))));
      setState(State.FALLS_ASLEEP);
    }
    match = wakesUp.matcher(record);
    if (match.matches()) {
      setTime(LocalDateTime.of(Integer.valueOf(match.group(1)), Integer.valueOf(match.group(2)),
          Integer.valueOf(match.group(3)), Integer.valueOf(match.group(4)),
          Integer.valueOf(match.group(5))));
      setState(State.WAKES_UP);
    }
  }

  public String toString() {
    return getId() + "\n" + getTime() + "\n" + getState() + "\n";
  }

  LocalDateTime getTime() {
    return time;
  }

  int getId() {
    return id;
  }

  State getState() {
    return state;
  }

  private void setTime(LocalDateTime time) {
    this.time = time;
  }

  void setId(int id) {
    this.id = id;
  }

  private void setState(State state) {
    this.state = state;
  }
}
