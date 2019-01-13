import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day10 {
  ArrayList<Pair<Integer, Integer>> positions;
  ArrayList<Pair<Integer, Integer>> velocities;
  char[][] grid;
  Pair<Integer, Integer> origin;
  Map<Pair<Integer, Integer>, Pair<Integer, Integer>> coord;
  private static final Pattern pattern =
      Pattern.compile("position=<\\s?(.+),\\s\\s?(.+)> velocity=<\\s?(.+),\\s\\s?(.+)>");

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
    while (!areColumnsNextToOneAnother()) {
      updatePositionWithVelocity();
    }

    positions.stream().forEach(p -> updateGridWithPosition(p));
    for (int second = 1; second <= 100; second++) {
      System.out.println("second " + second);
      System.out.println(positions.get(0));
      resetGrid(grid);
      //      updatePositionWithVelocity();
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
      System.out.println();
      System.out.println();
      System.out.println();
    }
    return "";
  }

  int getResult2() {
    return -1;
  }

  boolean areColumnsNextToOneAnother() {
    Set<Pair<Integer, Integer>> positionsSet =
        positions.stream().collect(Collectors.toCollection(TreeSet::new));
    Map<Integer, Integer> countColumnMap =
        positionsSet
            .stream()
            .map(p -> p.getValue0())
            .collect(
                HashMap::new,
                (a, v) -> {
                  a.put(v, a.getOrDefault(v, 0) + 1);
                },
                (a, b) -> {});
    Optional<Map.Entry<Integer, Integer>> columns10 =
        countColumnMap.entrySet().stream().filter(e -> e.getValue() >= 8).findFirst();
    if (columns10.isEmpty()) {
      return false;
    }
    return true;
//    //    System.out.println("column: " + columns10.get());
//    //    countColumnMap.entrySet().removeIf(e -> e.getValue() < 8);
//    //    countColumnMap.entrySet().stream().forEach(p -> System.out.print(p + " "));
//    List<Pair<Integer, Integer>> positionsToLookAt =
//        positionsSet
//            .stream()
//            .filter(p -> countColumnMap.containsKey(p.getValue0()))
//            .sorted(
//                Comparator.comparingInt((Pair<Integer, Integer> p) -> p.getValue0())
//                    .thenComparingInt(p -> p.getValue1()))
//            .collect(Collectors.toCollection(TreeSet::new))
//            .stream()
//            .collect(Collectors.toCollection(ArrayList::new));
////    positionsToLookAt.stream().forEach(p -> System.out.print(p + " "));
////    System.out.println();
//    int maxAdjacentColumns = 0;
//    int count = 0;
//
//    for (int i = 0; i < positionsToLookAt.size() - 1; i++) {
//      Pair<Integer, Integer> pi = positionsToLookAt.get(i);
//      Pair<Integer, Integer> pip = positionsToLookAt.get(i + 1);
//      if (pi.getValue0() == pip.getValue0() && pi.getValue1() + 1 == pip.getValue1()) {
//        //      System.out.print(pi + " " + pip);
//        //      System.out.println();
//        count++;
//                System.out.println("count: " + count + " ");
//      } else {
//        count = 0;
//      }
//
//      maxAdjacentColumns = Integer.max(maxAdjacentColumns, count);
//    }
//
//    //        System.out.print(maxAdjacentColumns + " ");
//    //        System.out.println();
//    return maxAdjacentColumns + 1 >= 8;
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
