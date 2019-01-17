import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day10 {

  ArrayList<Pair<Integer, Integer>> positions;
  ArrayList<Pair<Integer, Integer>> velocities;
  char[][] grid;
  Pair<Integer, Integer> origin;
  Map<Pair<Integer, Integer>, Pair<Integer, Integer>> coord;
  private static final Pattern pattern =
      Pattern.compile("position=<\\s?(.+),\\s\\s?(.+)> velocity=<\\s?(.+),\\s\\s?(.+)>");
  private static int times = 0;
  //  The magic number for the radius of a circle when all the positions are inside it and match
  //  the correct output
  final static int RADIUS = 11698;

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
    coord = new HashMap<>();
  }

  void resetGrid(char[][] grid) {
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        grid[i][j] = '.';
      }
    }
  }

  void reallocateGrid() {
    char[][] newGrid = new char[grid.length * 2][grid[0].length * 2];
    resetGrid(newGrid);
    Arrays.copyOfRange(grid, 0, grid.length);
    Pair<Integer, Integer> oldOrigin = origin;
    int m = grid.length;
    int n = grid[0].length;
    for (int i = newGrid.length - m; i < newGrid.length; i++) {
      for (int j = newGrid[0].length - n; j < newGrid[0].length; j++) {
        newGrid[i][j] = grid[i - m][j - n];
      }
    }
    grid = newGrid;
    origin = new Pair<>(grid.length / 2, grid[0].length / 2);
  }

  void updateGridWithPosition(Pair<Integer, Integer> position) {
    // x - columns
    // y - lines
    int x = position.getValue0();
    int y = position.getValue1();
    int oX = origin.getValue0();
    int oY = origin.getValue1();
    while (oX + Math.abs(x) >= grid[0].length || oY + Math.abs(y) >= grid.length) {
      reallocateGrid();
    }
    if (x >= 0 && y >= 0) {
      grid[oY + y][oX + x] = '#';
    } else if (x >= 0 && y <= 0) {
      grid[oY - Math.abs(y)][oX + x] = '#';
    } else if (x <= 0 && y >= 0) {
      grid[oY + y][oX - Math.abs(x)] = '#';
    } else {
      grid[oY - Math.abs(y)][oX - Math.abs(x)] = '#';
    }
  }

  void updatePositionWithVelocity() {
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
    areCoordinatesCloseEnough();
    positions.stream().forEach(p -> updateGridWithPosition(p));
    resetGrid(grid);
    positions.stream().forEach(p -> updateGridWithPosition(p));
    for (int i = 0; i < grid.length; i++) {
      char[] line = grid[i];
      if (IntStream.range(0, grid[i].length).allMatch(ii -> line[ii] != '#')) {
        continue;
      }
      for (int j = 0; j < grid.length; j++) {
        System.out.print(grid[i][j] + " ");
      }
      System.out.println();
    }
    return "";
  }

  int getResult2() {
    OptionalInt maxX = positions.stream().mapToInt(p -> Math.abs(p.getValue0())).max();
    OptionalInt maxY = positions.stream().mapToInt(p -> Math.abs(p.getValue0())).max();
    Pair<Integer, Integer> currentOrigin = new Pair<>(maxX.getAsInt() / 2, maxY.getAsInt() / 2);
    boolean arePointsInsideCircle = false;
    int seconds = 0;
    while (!arePointsInsideCircle) {
      // if all points are within the circle with above radius we stop, otherwise we continue
      updatePositionWithVelocity();
      seconds++;

      maxX = positions.stream().mapToInt(p -> Math.abs(p.getValue0())).max();
      maxY = positions.stream().mapToInt(p -> Math.abs(p.getValue0())).max();
      currentOrigin = new Pair<>(maxX.getAsInt() / 2, maxY.getAsInt() / 2);

      final Pair<Integer, Integer> lambaCurrentOrigin = currentOrigin;
      arePointsInsideCircle =
          positions
              .stream()
              .mapToInt(p -> distance(lambaCurrentOrigin, p))
              .allMatch(d -> d <= RADIUS);
    }
    return seconds;
  }

  boolean areCoordinatesCloseEnough() {
    OptionalInt maxX = positions.stream().mapToInt(p -> Math.abs(p.getValue0())).max();
    OptionalInt maxY = positions.stream().mapToInt(p -> Math.abs(p.getValue0())).max();
    Pair<Integer, Integer> currentOrigin = new Pair<>(maxX.getAsInt() / 2, maxY.getAsInt() / 2);
    boolean arePointsInsideCircle = false;
    while (!arePointsInsideCircle) {
      // if all points are within the circle with above radius we stop, otherwise we continue
      updatePositionWithVelocity();

      maxX = positions.stream().mapToInt(p -> Math.abs(p.getValue0())).max();
      maxY = positions.stream().mapToInt(p -> Math.abs(p.getValue0())).max();
      currentOrigin = new Pair<>(maxX.getAsInt() / 2, maxY.getAsInt() / 2);

      final Pair<Integer, Integer> lambaCurrentOrigin = currentOrigin;
      arePointsInsideCircle =
          positions
              .stream()
              .mapToInt(p -> distance(lambaCurrentOrigin, p))
              .allMatch(d -> d <= RADIUS);
    }
    return true;
  }

  private int distance(Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
    return (from.getValue0() - to.getValue0()) * (from.getValue0() - to.getValue0())
        + (from.getValue1() - to.getValue1()) * (from.getValue1() - to.getValue1());
  }

  public static void main(String[] args) {
    try {
      Day10 day10 = new Day10();
      day10.getResult1();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
