import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.javatuples.Pair;

class Day10 {

  private ArrayList<Pair<Integer, Integer>> positions;
  private ArrayList<Pair<Integer, Integer>> velocities;
  private char[][] grid;
  private Pair<Integer, Integer> origin;
  private static final Pattern pattern =
      Pattern.compile("position=<\\s?(.+),\\s\\s?(.+)> velocity=<\\s?(.+),\\s\\s?(.+)>");
  //  The magic number for the radius of a circle when all the positions are inside it and match
  //  the correct output
  private static final int SQUARE_RADIUS = 11698;

  Day10() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    positions = new ArrayList<>();
    velocities = new ArrayList<>();
    for (String line : lines) {
      Matcher match = pattern.matcher(line);
      if (match.matches()) {
        positions.add(new Pair<>(Integer.valueOf(match.group(1)), Integer.valueOf(match.group(2))));
        velocities.add(
            new Pair<>(Integer.valueOf(match.group(3)), Integer.valueOf(match.group(4))));
      }
    }
    grid = new char[128][128];
    origin = new Pair<>(grid.length / 2, grid[0].length / 2);
    resetGrid(grid);
  }

  private void resetGrid(char[][] grid) {
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        grid[i][j] = '.';
      }
    }
  }

  private void reallocateGrid() {
    char[][] newGrid = new char[grid.length * 2][grid[0].length * 2];
    resetGrid(newGrid);
    int m = grid.length;
    int n = grid[0].length;
    IntStream.range(m, newGrid.length)
        .forEach(i -> System.arraycopy(grid[i - m], 0, newGrid[i], n, n));
    grid = newGrid;
    origin = new Pair<>(grid.length / 2, grid[0].length / 2);
  }

  private void updateGridWithPosition(Pair<Integer, Integer> position) {
    // x - columns
    // y - lines
    int x = position.getValue0();
    int y = position.getValue1();
    int ox = origin.getValue0();
    int oy = origin.getValue1();
    while (ox + Math.abs(x) >= grid[0].length || oy + Math.abs(y) >= grid.length) {
      reallocateGrid();
    }
    if (x >= 0 && y >= 0) {
      grid[oy + y][ox + x] = '#';
    } else if (x >= 0) {
      grid[oy - Math.abs(y)][ox + x] = '#';
    } else if (y >= 0) {
      grid[oy + y][ox - Math.abs(x)] = '#';
    } else {
      grid[oy - Math.abs(y)][ox - Math.abs(x)] = '#';
    }
  }

  private void updatePositionWithVelocity() {
    IntStream.range(0, positions.size())
        .forEach(
            i -> {
              Pair<Integer, Integer> position = positions.get(i);
              Pair<Integer, Integer> velocity = velocities.get(i);
              position =
                  position
                      .setAt0(position.getValue0() + velocity.getValue0())
                      .setAt1(position.getValue1() + velocity.getValue1());
              positions.set(i, position);
            });
  }

  String getResult1() {
    while (coordinatesNotInsideCircle()) {
      updatePositionWithVelocity();
    }
    positions.forEach(this::updateGridWithPosition);
    Pair<Integer, Integer> minMaxColumn = getMinMaxColumn();
    StringBuilder output = new StringBuilder();
    for (char[] line : grid) {
      if (IntStream.range(0, line.length).allMatch(ii -> line[ii] != '#')) {
        continue;
      }
      for (int j = minMaxColumn.getValue0(); j <= minMaxColumn.getValue1(); j++) {
        output.append(line[j]);
      }
      output.append('\n');
    }
    return output.toString();
  }

  int getResult2() {
    int seconds = 0;
    while (coordinatesNotInsideCircle()) {
      updatePositionWithVelocity();
      seconds++;
    }
    return seconds;
  }

  private boolean coordinatesNotInsideCircle() {
    OptionalInt maxX = positions.stream().mapToInt(p -> Math.abs(p.getValue0())).max();
    OptionalInt maxY = positions.stream().mapToInt(p -> Math.abs(p.getValue1())).max();
    int max = 0;
    if (maxX.isPresent()) {
      max = Integer.max(maxX.getAsInt(), maxY.getAsInt());
    }
    final Pair<Integer, Integer> currentOrigin = new Pair<>(max / 2, max / 2);
    return !positions
        .stream()
        .mapToInt(p -> distance(currentOrigin, p))
        .allMatch(d -> d <= SQUARE_RADIUS);
  }

  private int distance(Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
    return (from.getValue0() - to.getValue0()) * (from.getValue0() - to.getValue0())
        + (from.getValue1() - to.getValue1()) * (from.getValue1() - to.getValue1());
  }

  private Pair<Integer, Integer> getMinMaxColumn() {
    int min = Integer.MAX_VALUE;
    int max = 0;
    for (char[] line : grid) {
      for (int j = 0; j < line.length; j++) {
        if (line[j] == '#') {
          min = Integer.min(min, j);
          break;
        }
      }
    }
    for (int i = grid.length - 1; i >= 0; i--) {
      for (int j = grid[0].length - 1; j >= 0; j--) {
        if (grid[i][j] == '#') {
          max = Integer.max(max, j);
          break;
        }
      }
    }
    return new Pair<>(min, max);
  }
}
