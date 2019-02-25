import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.javatuples.Pair;

class Day15 {

  List<Square> area;
  List<Unit> units;
  final int n;
  final int m;

  Day15() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    n = lines.length;
    m = lines[0].length();
    area = new ArrayList<>();
    IntStream.range(0, lines.length)
        .forEach(
            i ->
                IntStream.range(0, lines[i].length())
                    .forEach(
                        j -> area.add(new Square(new Pair<>(i, j), lines[i].toCharArray()[j]))));
    units = new ArrayList<>();
    for (Square square : area) {
      if (square.getSymbol() == 'G') {
        Unit goblin = new Unit(square.getPosition(), UnitType.GOBLIN);
        units.add(goblin);
      } else if (square.getSymbol() == 'E') {
        Unit elf = new Unit(square.getPosition(), UnitType.ELF);
        units.add(elf);
      }
    }
  }

  List<Square> getAdjacentSquares(Square currentSquare) {
    List<Square> adjacentSquares = new ArrayList<>();
    int i = currentSquare.getPosition().getValue0();
    int j = currentSquare.getPosition().getValue1();
    // left
    Square square = area.get(i * m + (j - 1));
    if (square.getSymbol() == '.') {
      adjacentSquares.add(square);
    }
    // right
    square = area.get(i * m + (j + 1));
    if (square.getSymbol() == '.') {
      adjacentSquares.add(square);
    }
    // up
    square = area.get((i - 1) * m + j);
    if (square.getSymbol() == '.') {
      adjacentSquares.add(square);
    }
    // down
    square = area.get((i + 1) * m + j);
    if (square.getSymbol() == '.') {
      adjacentSquares.add(square);
    }
    return adjacentSquares;
  }

  List<Square> aStarPathFinding(Square from, Square to) {
    List<Square> path = new ArrayList<>();
    List<Square> openList = new ArrayList<>();
    List<Square> closedList = new ArrayList<>();

    openList.add(from);
    while (!openList.isEmpty()) {
      Square currentSquare = openList.stream().min(Comparator.comparing(Square::getF)).get();
      openList.remove(currentSquare);
      closedList.add(currentSquare);

      if (currentSquare.getPosition().equals(to.getPosition())) {
        Square current = currentSquare;
        while (current != null) {
          path.add(current);
          current = current.parent;
        }
        return path;
      }
      List<Square> adjacentSquares = getAdjacentSquares(currentSquare);
      for (Square square : adjacentSquares) {
        square.setParent(currentSquare);
        if (closedList.stream()
            .filter(s -> s.getPosition().equals(square.getPosition()))
            .findFirst()
            .isPresent()) {
          continue;
        }

        square.setG(currentSquare.getG() + 1);
        square.setH(manhattanDistance(square, to));
        square.setF(square.getG() + square.getH());

        Optional<Square> openListSquareOptional =
            openList.stream().filter(s -> s.getPosition().equals(square.getPosition())).findFirst();
        if (openListSquareOptional.isPresent()) {
          Square openListSquare = openListSquareOptional.get();
          int squareG = openListSquare.getG();
          if (square.getG() > squareG) {
            continue;
          }
        }

        openList.add(square);
      }
    }
    return path;
  }

  int manhattanDistance(Square from, Square to) {
    int x1 = from.getPosition().getValue0();
    int y1 = from.getPosition().getValue1();
    int x2 = to.getPosition().getValue0();
    int y2 = to.getPosition().getValue1();

    return Math.abs(x1 - x2) + Math.abs(y1 - y2);
  }

  Square getSquareForUnit(Unit unit) {
    return area.stream().filter(s -> s.getPosition().equals(unit.getPosition())).findFirst().get();
  }

  int getResult1() {
    for (int round = 0; round < 1; round++) {
      // iterate each unit in  reading order
      for (Unit unit : units) {
        List<Square> targetsInRangePositions;
        // find targets in range for each type of unit
        if (unit.getUnitType() == UnitType.ELF) {
          targetsInRangePositions =
              units.stream()
                  .filter(u -> u.getUnitType() == UnitType.GOBLIN)
                  .flatMap(u -> getAdjacentSquares(getSquareForUnit(u)).stream())
                  .collect(Collectors.toCollection(ArrayList::new));
        } else {
          targetsInRangePositions =
              units.stream()
                  .filter(u -> u.getUnitType() == UnitType.ELF)
                  .flatMap(u -> getAdjacentSquares(getSquareForUnit(u)).stream())
                  .collect(Collectors.toCollection(ArrayList::new));
        }
        targetsInRangePositions.stream().forEach(t -> System.out.println(aStarPathFinding(getSquareForUnit(unit), t)));
      }
    }
    return -1;
  }

  int getResult2() {
    return -1;
  }

  static class Square {
    int g;
    Square parent;

    public Square getParent() {
      return parent;
    }

    public void setParent(Square parent) {
      this.parent = parent;
    }

    @Override
    public String toString() {
      return "Square{" +
              "g=" + g +
              ", h=" + h +
              ", f=" + f +
              ", position=" + position +
              ", symbol=" + symbol +
              '}';
    }

    public Square(Pair<Integer, Integer> position, char symbol) {
      this.position = position;
      this.symbol = symbol;
      this.g = 0;
      this.f = 0;
      this.h = 0;
      this.parent = null;
    }

    public int getG() {
      return g;
    }

    public void setG(int g) {
      this.g = g;
    }

    public int getH() {
      return h;
    }

    public void setH(int h) {
      this.h = h;
    }

    public int getF() {
      return f;
    }

    public void setF(int f) {
      this.f = f;
    }

    public Pair<Integer, Integer> getPosition() {
      return position;
    }

    public void setPosition(Pair<Integer, Integer> position) {
      this.position = position;
    }

    int h;

    public char getSymbol() {
      return symbol;
    }

    public void setSymbol(char symbol) {
      this.symbol = symbol;
    }

    int f;
    Pair<Integer, Integer> position;
    char symbol;
  }

  static class Unit {

    int hp;

    public int getHp() {
      return hp;
    }

    public void setHp(int hp) {
      this.hp = hp;
    }

    public Pair<Integer, Integer> getPosition() {
      return position;
    }

    public void setPosition(Pair<Integer, Integer> position) {
      this.position = position;
    }

    public UnitType getUnitType() {
      return unitType;
    }

    public void setUnitType(UnitType unitType) {
      this.unitType = unitType;
    }

    public Unit(Pair<Integer, Integer> position, UnitType unitType) {
      this.position = position;
      this.unitType = unitType;
    }

    public static final int ATTACK_POWER = 3;
    Pair<Integer, Integer> position;
    UnitType unitType;
  }

  enum UnitType {
    ELF,
    GOBLIN
  }
}
