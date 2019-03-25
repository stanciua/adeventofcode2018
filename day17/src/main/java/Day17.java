import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

class Day17 {
  char[][] slice2D;
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
    int maxY =
        clayLocations.stream()
            .map(p -> p.getValue0())
            .max(Comparator.comparing(Integer::intValue))
            .orElseThrow();
    int maxX =
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

    //    displaySlice2D();
  }

  void displaySlice2D() {
    for (var row : slice2D) {
      for (var col : row) {
        System.out.print(col);
      }
      System.out.println();
    }
  }

  void getClayCorners(
      List<Pair<Integer, Integer>> leftCorners, List<Pair<Integer, Integer>> rightCorners) {
    int height = slice2D.length;
    int width = slice2D[0].length;

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        char square = slice2D[i][j];
        if (square == '#'
            && i - 1 >= 0
            && j + 1 < width
            && slice2D[i - 1][j] == '#'
            && slice2D[i][j + 1] == '#') {
          leftCorners.add(new Pair<>(i, j));
        }
        if (square == '#'
            && i - 1 >= 0
            && j - 1 < width
            && slice2D[i - 1][j] == '#'
            && slice2D[i][j - 1] == '#') {
          rightCorners.add(new Pair<>(i, j));
        }
      }
    }
  }

  void buildClayList(
      List<Clay> clays,
      List<Pair<Integer, Integer>> leftCorners,
      List<Pair<Integer, Integer>> rightCorners) {
    for (var leftCorner : leftCorners) {
      for (var rightCorner : rightCorners) {
        int y3 = leftCorner.getValue0();
        int x3 = leftCorner.getValue1();
        int y4 = rightCorner.getValue0();
        int x4 = rightCorner.getValue1();
        int x1 = x3;
        int y1 = y3;
        int x2 = x4;
        int y2 = y4;
        // if this is a bottom
        if (y3 == y4 && IntStream.rangeClosed(x3, x4).allMatch(x -> slice2D[y3][x] == '#')) {
          for (int i = y3; i >= 0; i--) {
            if (slice2D[i][x3] != '#') {
              y1 = i + 1;
              break;
            }
          }
          for (int i = y4; i >= 0; i--) {
            if (slice2D[i][x4] != '#') {
              y2 = i + 1;
              break;
            }
          }

          clays.add(
              new Clay(
                  new Pair<>(y1, x1), new Pair<>(y2, x2), new Pair<>(y3, x3), new Pair<>(y4, x4)));
        }
      }
    }
  }

  boolean didWaterReachBottom(Pair<Integer, Integer> waterPosition, List<Clay> clayList) {
    for (Clay clay : clayList) {
      int y3 = clay.getDownLeft().getValue0();
      int x3 = clay.getDownLeft().getValue1();
      int y4 = clay.getDownRight().getValue0();
      int x4 = clay.getDownRight().getValue1();
      int y = waterPosition.getValue0();
      int x = waterPosition.getValue1();
      
      if (y == y3 - 1 && x > x3 && x < x4) {
        // we have reached bottom of the clay
        return true;
      }
    }
    return false;
  }

  int getResult1() {
    List<Pair<Integer, Integer>> leftCorners = new ArrayList<>();
    List<Pair<Integer, Integer>> rightCorners = new ArrayList<>();
    getClayCorners(leftCorners, rightCorners);
    List<Clay> clayList = new ArrayList<>();
    buildClayList(clayList, leftCorners, rightCorners);
    clayList.forEach(System.out::println);
    List<Pair<Integer, Integer>> waterFlows = new ArrayList<>();
    waterFlows.add(new Pair<>(0, 500));
    return -1;
  }

  static class Clay {

    public Clay(
        Pair<Integer, Integer> upperLeft,
        Pair<Integer, Integer> upperRight,
        Pair<Integer, Integer> downLeft,
        Pair<Integer, Integer> downRight) {
      this.upperLeft = upperLeft;
      this.upperRight = upperRight;
      this.downLeft = downLeft;
      this.downRight = downRight;
    }

    @Override
    public String toString() {
      return "Clay{"
          + "upperLeft="
          + upperLeft
          + ", upperRight="
          + upperRight
          + ", downLeft="
          + downLeft
          + ", downRight="
          + downRight
          + '}';
    }

    public Pair<Integer, Integer> getUpperLeft() {
      return upperLeft;
    }

    public void setUpperLeft(Pair<Integer, Integer> upperLeft) {
      this.upperLeft = upperLeft;
    }

    public Pair<Integer, Integer> getUpperRight() {
      return upperRight;
    }

    public void setUpperRight(Pair<Integer, Integer> upperRight) {
      this.upperRight = upperRight;
    }

    public Pair<Integer, Integer> getDownLeft() {
      return downLeft;
    }

    public void setDownLeft(Pair<Integer, Integer> downLeft) {
      this.downLeft = downLeft;
    }

    public Pair<Integer, Integer> getDownRight() {
      return downRight;
    }

    public void setDownRight(Pair<Integer, Integer> downRight) {
      this.downRight = downRight;
    }

    private Pair<Integer, Integer> upperLeft;
    private Pair<Integer, Integer> upperRight;
    private Pair<Integer, Integer> downLeft;
    private Pair<Integer, Integer> downRight;
  }

  Square getNextSquareInWaterflow(Square lastSquare) {
    return lastSquare;
  }

  int getResult2() {
    return -1;
  }

  static class Square {
    public Square(Pair<Integer, Integer> position) {
      this.position = position;
    }

    public Direction getDirection() {
      return direction;
    }

    public void setDirection(Direction direction) {
      this.direction = direction;
    }

    public Pair<Integer, Integer> getPosition() {
      return position;
    }

    public void setPosition(Pair<Integer, Integer> position) {
      this.position = position;
    }

    @Override
    public String toString() {
      return "Square{"
          + "direction="
          + direction
          + ", position="
          + position
          + ", symbol="
          + symbol
          + '}';
    }

    public char getSymbol() {
      return symbol;
    }

    public void setSymbol(char symbol) {
      this.symbol = symbol;
    }

    Direction direction;
    Pair<Integer, Integer> position;
    char symbol;
  }

  enum Direction {
    DOWN,
    LEFT,
    RIGHT
  }
}
