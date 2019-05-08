import org.javatuples.Triplet;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day23 {
  List<Nanobot> nanobots;
  private static final Pattern nanoBotPattern =
      Pattern.compile("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)");

  Day23() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    int x = 0;
    int y = 0;
    int z = 0;
    nanobots = new ArrayList<>();
    for (var line : lines) {
      Matcher matcher = nanoBotPattern.matcher(line);
      if (matcher.matches()) {
        Triplet<Long, Long, Long> position =
            new Triplet<>(
                Long.parseLong(matcher.group(1)),
                Long.parseLong(matcher.group(2)),
                Long.parseLong(matcher.group(3)));
        long radius = Long.parseLong(matcher.group(4));
        nanobots.add(new Nanobot(position, radius));
      }
    }
  }

  int getResult1() {
    Nanobot strongestNanobot =
        nanobots.stream().max(Comparator.comparing(Nanobot::getRadius)).orElseThrow();
    long strongestNanobotSignal = strongestNanobot.getRadius();
    int countInRange = 0;
    for (Nanobot nanobot : nanobots) {
      if (getManhattanDistance(strongestNanobot.getPosition(), nanobot.getPosition())
          <= strongestNanobotSignal) {
        countInRange++;
      }
    }

    return countInRange;
  }

  long getManhattanDistance(
      Triplet<Long, Long, Long> from, Triplet<Long, Long, Long> to) {
    return Math.abs(from.getValue0() - to.getValue0())
        + Math.abs(from.getValue1() - to.getValue1())
        + Math.abs(from.getValue2() - to.getValue2());
  }

  int getResult2() {
    long minX = Long.MAX_VALUE;
    long minY = Long.MAX_VALUE;
    long minZ = Long.MAX_VALUE;
    for (Nanobot nanobot: nanobots) {
      minX = Long.min(minX, nanobot.getPosition().getValue0());
      minY = Long.min(minY, nanobot.getPosition().getValue1());
      minZ = Long.min(minZ, nanobot.getPosition().getValue2());
    }

    System.out.println(minX + ", " + minY + ", " + minZ);
    return -1;
  }

  static class Nanobot {
    Triplet<Long, Long, Long> position;
    long radius;

    @Override
    public String toString() {
      return "Nanobot{" + "position=" + position + ", radius=" + radius + '}';
    }

    public Triplet<Long, Long, Long> getPosition() {
      return position;
    }

    public long getRadius() {
      return radius;
    }

    public Nanobot(Triplet<Long, Long, Long> position, long radius) {
      this.position = position;
      this.radius = radius;
    }
  }
}
