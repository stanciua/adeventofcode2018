import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.javatuples.Triplet;

class Day17 {
  char[][] slice2D;
  final int maxX;
  final int maxY;
  private static final Pattern veinsOfClay =
      Pattern.compile("(x|y)=(\\d+),\\s(x|y)=(\\d+)\\.\\.(\\d+)");

  Day17() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    int x = 0;
    int y = 0;
    Pair<Integer, Integer> rangeX = new Pair<>(0, 0);
    Pair<Integer, Integer> rangeY = new Pair<>(0, 0);
    List<Pair<Integer, Integer>> clayLocations = new ArrayList<>();
    for (var line : lines) {
      Matcher matcher = veinsOfClay.matcher(line);
      boolean isXFirst = false;
      if (matcher.matches()) {
        if (matcher.group(1).equals("x")) {
          isXFirst = true;
          x = Integer.parseInt(matcher.group(2));
        } else {
          y = Integer.parseInt(matcher.group(2));
        }

        if (matcher.group(3).equals("x")) {
          rangeX = rangeX.setAt0(Integer.parseInt(matcher.group(4)));
          rangeX = rangeX.setAt1(Integer.parseInt(matcher.group(5)));
        } else {
          rangeY = rangeY.setAt0(Integer.parseInt(matcher.group(4)));
          rangeY = rangeY.setAt1(Integer.parseInt(matcher.group(5)));
        }

        final int xFinal = x;
        final int yFinal = y;
        if (isXFirst) {
          IntStream.rangeClosed(rangeY.getValue0(), rangeY.getValue1())
              .forEach(yc -> clayLocations.add(new Pair<>(yc, xFinal)));
        } else {
          IntStream.rangeClosed(rangeX.getValue0(), rangeX.getValue1())
              .forEach(xc -> clayLocations.add(new Pair<>(yFinal, xc)));
        }
      }
    }
    maxY =
        clayLocations.stream()
            .map(p -> p.getValue0())
            .max(Comparator.comparing(Integer::intValue))
            .orElseThrow();
    maxX =
        clayLocations.stream()
            .map(p -> p.getValue1())
            .max(Comparator.comparing(Integer::intValue))
            .orElseThrow();

    slice2D = new char[maxY + 1][maxX + 1];
    // initialize each cell with sand '.'
    for (int i = 0; i < slice2D.length; i++) {
      for (int j = 0; j < slice2D[i].length; j++) {
        slice2D[i][j] = '.';
      }
    }
    // initialize water spring
    slice2D[0][500] = '+';

    // initialize the clay cells with '#'
    clayLocations.stream().forEach(c -> slice2D[c.getValue0()][c.getValue1()] = '#');

    displaySlice2D();
  }

  void fillWater(char[][] slice2D, int row, int col) {
    while (true) {
      if (row > maxY) {
        return;
      }

      if (slice2D[row][col] == '#' || slice2D[row][col] == '~') {
        // can it hold water?
        var holdingWater = canHoldWater(slice2D, row, col);
        if (holdingWater.getValue0()) {
          int leftClay = holdingWater.getValue1();
          int rightClay = holdingWater.getValue2();
          final int rowFinal = row;
          IntStream.range(leftClay + 1, rightClay).forEach(i -> slice2D[rowFinal - 1][i] = '~');
          row -= 2;
        } else {
          // if we cannot hold water we need to see in which direction the water will flow:
          //  - left = 1
          //  - right = 2
          //  - left and right = 3
          int direction = flowOfWaterDirection(slice2D, row, col);
        }
      }
    }
  }
  int flowOfWaterDirection(char[][] slice2D, int row, int col) {
    return -1;
  }
  Triplet<Boolean, Integer, Integer> canHoldWater(char[][] slice2D, int x, int y) {
    int firstLeftClayPosition = -1;
    int firstRightClayPosition = -1;

    for (int i = x + 1; i <= maxX; i++) {
      if (slice2D[y - 1][i] == '#') {
        firstRightClayPosition = i;
        break;
      }
    }

    for (int i = x - 1; i <= 0; i++) {
      if (slice2D[y - 1][i] == '#') {
        firstLeftClayPosition = i; break; } }

    if (firstLeftClayPosition == -1 || firstRightClayPosition == -1) {
      return new Triplet<>(false, -1, -1);
    }

    // now we need to make sure that we have a bottom for the water to sit on
    return new Triplet<>(
        IntStream.rangeClosed(firstLeftClayPosition, firstRightClayPosition)
            .allMatch(idx -> slice2D[y][idx] == '#' || slice2D[y][idx] == '~'),
        firstLeftClayPosition,
        firstRightClayPosition);
  }

  void displaySlice2D() {
    for (var row : slice2D) {
      for (var col : row) {
        System.out.print(col);
      }
      System.out.println();
    }
  }

  int getResult1() {
    return -1;
  }

  int getResult2() {
    return -1;
  }
}
