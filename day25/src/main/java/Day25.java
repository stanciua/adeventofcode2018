import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.javatuples.Quartet;

class Day25 {
  private final Set<Quartet<Integer, Integer, Integer, Integer>> points = new HashSet<>();
  private final Set<Set<Quartet<Integer, Integer, Integer, Integer>>> constellations = new HashSet<>();

  Day25() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    for (var line : lines) {
      String[] values = line.split(",");
      points.add(
          new Quartet<>(
              Integer.valueOf(values[0].strip()),
              Integer.valueOf(values[1].strip()),
              Integer.valueOf(values[2].strip()),
              Integer.valueOf(values[3].strip())));
    }
  }

  private int getManhattanDistance(
      Quartet<Integer, Integer, Integer, Integer> p1,
      Quartet<Integer, Integer, Integer, Integer> p2) {
    return Math.abs(p1.getValue0() - p2.getValue0())
        + Math.abs(p1.getValue1() - p2.getValue1())
        + Math.abs(p1.getValue2() - p2.getValue2())
        + Math.abs(p1.getValue3() - p2.getValue3());
  }

  int getResult1() {
    var newConstellations = new HashSet<HashSet<Quartet<Integer, Integer, Integer, Integer>>>();
    for (var p : points) {
      boolean found = false;
      constellations.addAll(newConstellations);
      newConstellations.clear();
      for (var constellation : constellations) {
        var newConstellation = new ArrayList<Quartet<Integer, Integer, Integer, Integer>>();
        for (var pi : constellation) {
          if (getManhattanDistance(p, pi) <= 3) {
            found = true;
            newConstellation.add(p);
            break;
          }
        }
        constellation.addAll(newConstellation);
      }
      if (!found) {
        var constellation = new HashSet<Quartet<Integer, Integer, Integer, Integer>>();
        constellation.add(p);
        newConstellations.add(constellation);
      }
    }
    mergeConstellations();
    return constellations.size();
  }

  private void mergeConstellations() {
    boolean foundMerge = false;
    keepMerging:
    while (true) {
      var constellationsList = new ArrayList<>(constellations);
      for (int i = 0; i < constellationsList.size() - 1; i++) {
        for (int j = i + 1; j < constellationsList.size(); j++) {
          var intersection = new HashSet<>(constellationsList.get(i));
          intersection.retainAll(constellationsList.get(j));
          if (!intersection.isEmpty()) {
            foundMerge = true;
            var mergedConstellation = new HashSet<>(constellationsList.get(i));
            mergedConstellation.addAll(constellationsList.get(j));
            final var fi = i;
            final var fj = j;
            constellations.removeIf(c -> c == constellationsList.get(fi));
            constellations.removeIf(c -> c == constellationsList.get(fj));
            constellations.add(mergedConstellation);
            continue keepMerging;
          }
        }
      }
      // if we end up here and no merge was found, we're done
      if (!foundMerge) {
        break;
      }
      foundMerge = false;
    }
  }
}
