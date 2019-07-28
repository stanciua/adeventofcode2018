import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.javatuples.Pair;
import org.javatuples.Triplet;

class Day23 {
  private List<Nanobot> nanobots;
  private static final Pattern nanoBotPattern =
      Pattern.compile("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)");

  Day23() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
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

  private static long getManhattanDistance(
      Triplet<Long, Long, Long> from, Triplet<Long, Long, Long> to) {
    return Math.abs(from.getValue0() - to.getValue0())
        + Math.abs(from.getValue1() - to.getValue1())
        + Math.abs(from.getValue2() - to.getValue2());
  }

  long getResult2() {
    var neighbors =
        nanobots.stream()
            .map(
                n ->
                    new Pair<>(
                        n,
                        nanobots.stream()
                            .filter(
                                nanobot ->
                                    n.position != nanobot.position
                                        && n.isNanobotInRangeOfOther(nanobot))
                            .collect(Collectors.toCollection(HashSet::new))))
            .collect(Collectors.toMap(Pair::getValue0, Pair::getValue1));

    var bronKerbosch = new BronKerbosch(neighbors);
    var clique = bronKerbosch.largestClique();
    var origin = new Triplet<>(0L, 0L, 0L);
    return clique.stream()
        .map(n -> Day23.getManhattanDistance(n.position, origin) - n.radius)
        .max(Comparator.comparingLong(Long::longValue))
        .orElseThrow();
  }
  // Algorithm source: https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm
  // Part 2 solution inspired by: https://todd.ginsberg.com/post/advent-of-code/2018/day23/

  static class BronKerbosch {
    private Set<Nanobot> bestR = new HashSet<>();

    Set<Nanobot> largestClique() {
      execute(neighbors.keySet(), new HashSet<>(), new HashSet<>());
      return bestR;
    }

    private void execute(Set<Nanobot> p, Set<Nanobot> r, Set<Nanobot> x) {
      if (p.isEmpty() && x.isEmpty()) {
        if (r.size() > bestR.size()) {
          bestR = r;
        }
      } else {
        var punionx = new HashSet<>(p);
        punionx.addAll(x);
        Nanobot mostNeighborsOfPUnionX =
            punionx.stream()
                .max(Comparator.comparingInt(n -> neighbors.get(n).size()))
                .orElseThrow();
        var pwithoutneighbors = new HashSet<>(p);
        pwithoutneighbors.removeAll(neighbors.get(mostNeighborsOfPUnionX));
        for (var v : pwithoutneighbors) {
          // P intersect neighbors of v
          var neighborsOfV = neighbors.get(v);
          var pintersectneighborsofv = new HashSet<>(p);
          pintersectneighborsofv.retainAll(neighborsOfV);
          // r + v
          var runionv = new HashSet<>(r);
          runionv.add(v);
          // X intersect neighbors of v
          var xintersectneighborsofv = new HashSet<>(x);
          xintersectneighborsofv.retainAll(neighborsOfV);
          execute(pintersectneighborsofv, runionv, xintersectneighborsofv);
        }
      }
    }

    BronKerbosch(Map<Nanobot, HashSet<Nanobot>> neighbors) {
      this.neighbors = neighbors;
    }

    final Map<Nanobot, HashSet<Nanobot>> neighbors;
  }

  static class Nanobot {
    final Triplet<Long, Long, Long> position;
    final long radius;

    @Override
    public String toString() {
      return "Nanobot{" + "position=" + position + ", radius=" + radius + '}';
    }

    Triplet<Long, Long, Long> getPosition() {
      return position;
    }

    long getRadius() {
      return radius;
    }

    Nanobot(Triplet<Long, Long, Long> position, long radius) {
      this.position = position;
      this.radius = radius;
    }

    boolean isNanobotInRangeOfOther(Nanobot other) {
      return Day23.getManhattanDistance(this.position, other.position)
          <= this.radius + other.radius;
    }
  }
}
