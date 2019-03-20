import org.javatuples.Pair;

import javax.sound.midi.SysexMessage;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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

    displaySlice2D();
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
    List<Square> waterFlow = new ArrayList<>();
    waterFlow.add(new Square(new Pair<>(500, 1)));
    Square lastSquare = waterFlow.get(waterFlow.size() - 1);
    lastSquare.setDirection(Direction.DOWN);
    lastSquare.setSymbol('|');
    while (lastSquare.getPosition().getValue0() >= 1
        && lastSquare.getPosition().getValue0() < slice2D.length) {
      Square nextSquare = getNextSquareInWaterflow(lastSquare);
      break;
    }
    return -1;
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
