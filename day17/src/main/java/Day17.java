import java.nio.CharBuffer;
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
  private char[][] slice2D;
  private final int maxX;
  private final int maxY;
  private final int minY;
  private static final Pattern veinsOfClay =
      Pattern.compile("([xy])=(\\d+),\\s([xy])=(\\d+)\\.\\.(\\d+)");

  Day17() throws Exception {
    int x = 0;
    int y = 0;
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
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
        int xFinal = x;
        int yFinal = y;
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
            .map(Pair::getValue0)
            .max(Comparator.comparing(Integer::intValue))
            .orElseThrow();

    minY =
        clayLocations.stream()
            .map(Pair::getValue0)
            .min(Comparator.comparing(Integer::intValue))
            .orElseThrow();

    maxX =
        clayLocations.stream()
            .map(Pair::getValue1)
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
    clayLocations.forEach(c -> slice2D[c.getValue0()][c.getValue1()] = '#');
  }

  private void fillWater(char[][] slice2D, int row, int col) {
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
          row -= 1;
        } else {
          var canFlowLeft = flowOfWaterDirection(slice2D, row - 1, col, Direction.LEFT);
          var canFlowRight = flowOfWaterDirection(slice2D, row - 1, col, Direction.RIGHT);
          waterFlowLeft(canFlowLeft.getValue0(), canFlowLeft.getValue1(), row, col);
          waterFlowRight(canFlowRight.getValue0(), canFlowRight.getValue1(), row, col);
          return;
        }
      } else {
        // water always flows down
        slice2D[row][col] = '|';
        row++;
      }
    }
  }

  private void waterFlowLeft(boolean canFlow, int from, int row, int col) {
    if (canFlow) {
      IntStream.range(from, col).forEach(i -> slice2D[row - 1][i] = '|');
      fillWater(slice2D, row, from);
    } else {
      IntStream.range(from + 1, col)
          .forEach(
              i -> {
                if (slice2D[row - 1][i] != '~') slice2D[row - 1][i] = '|';
              });
    }
  }

  private void waterFlowRight(boolean canFlow, int to, int row, int col) {
    if (canFlow) {
      IntStream.range(col, to + 1).forEach(i -> slice2D[row - 1][i] = '|');
      fillWater(slice2D, row - 1, to);
    } else {
      IntStream.range(col, to)
          .forEach(
              i -> {
                if (slice2D[row - 1][i] != '~') slice2D[row - 1][i] = '|';
              });
    }
  }

  private Pair<Boolean, Integer> flowOfWaterDirection(
      char[][] slice2D, int row, int col, Direction direction) {
    if (direction == Direction.LEFT) {
      for (int i = col - 1; i >= 0; i--) {
        char firstClayLeft = slice2D[row][i];
        char firstClayLeftLeft = 'x';
        if (i - 1 >= 0) {
          firstClayLeftLeft = slice2D[row][i - 1];
        }
        char firstClayLeftDown = slice2D[row + 1][i];
        if (firstClayLeft == '#'
            || ((firstClayLeft == '|' && firstClayLeftDown == '|')
                || (firstClayLeft == '|' && firstClayLeftLeft == '|'))) {
          // we cannot go any further and we should stop
          return new Pair<>(false, i);
        } else if (firstClayLeft == '.' && firstClayLeftDown == '.') {
          return new Pair<>(true, i);
        }
      }
      return new Pair<>(false, -1);
    } else {
      for (int i = col + 1; i <= maxX; i++) {
        char firstClayRight = slice2D[row][i];
        char firstClayRightRight = 'x';
        if (i + 1 <= maxX) {
          firstClayRightRight = slice2D[row][i + 1];
        }
        char firstClayRightDown = slice2D[row + 1][i];
        if (firstClayRight == '#'
            || ((firstClayRight == '|' && firstClayRightDown == '|')
                || (firstClayRight == '|' && firstClayRightRight == '|'))) {
          // we cannot go any further and we should stop
          return new Pair<>(false, i);
        }
        if (firstClayRight == '.' && firstClayRightDown == '.') {
          return new Pair<>(true, i);
        }
      }
      return new Pair<>(false, -1);
    }
  }

  private Triplet<Boolean, Integer, Integer> canHoldWater(char[][] slice2D, int y, int x) {
    int firstLeftClayPosition = -1;
    int firstRightClayPosition = -1;

    for (int i = x + 1; i <= maxX; i++) {
      if (slice2D[y - 1][i] == '#') {
        firstRightClayPosition = i;
        break;
      }
    }

    for (int i = x - 1; i >= 0; i--) {
      if (slice2D[y - 1][i] == '#') {
        firstLeftClayPosition = i;
        break;
      }
    }

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

  int getResult1() {
    fillWater(slice2D, 1, 500);
    return (int)
        IntStream.rangeClosed(minY, maxY)
            .flatMap(i -> CharBuffer.wrap(slice2D[i]).chars())
            .filter(c -> c == '~' || c == '|')
            .count();
  }

  int getResult2() {
    fillWater(slice2D, 1, 500);
    return (int)
        IntStream.rangeClosed(minY, maxY)
            .flatMap(i -> CharBuffer.wrap(slice2D[i]).chars())
            .filter(c -> c == '~')
            .count();
  }

  enum Direction {
    LEFT,
    RIGHT
  }
}
