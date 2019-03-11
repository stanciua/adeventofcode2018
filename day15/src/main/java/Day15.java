import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.javatuples.Pair;

class Day15 {

  List<Square> area;
  List<Unit> units;
  List<Square> dots;
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

    dots =
        area.stream()
            .filter(d -> d.getSymbol() == '.')
            .collect(Collectors.toCollection(ArrayList::new));
  }

  List<Square> getAdjacentSquares(Square currentSquare) {
    List<Square> adjacentSquares = new ArrayList<>();
    int i = currentSquare.getPosition().getValue0();
    int j = currentSquare.getPosition().getValue1();
    // up
    Square square = area.get((i - 1) * m + j);
    if (square.getSymbol() == '.') {
      adjacentSquares.add(square);
    }
    // left
    square = area.get(i * m + (j - 1));
    if (square.getSymbol() == '.') {
      adjacentSquares.add(square);
    }
    // right
    square = area.get(i * m + (j + 1));
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
    area.stream().forEach(s -> s.resetState());
    while (!openList.isEmpty()) {
      Square currentSquare =
          openList.stream().sorted(Comparator.comparing(Square::getF)).findFirst().get();
      openList.remove(currentSquare);
      closedList.add(currentSquare);

      if (currentSquare.getPosition().equals(to.getPosition())) {
        Square current = currentSquare;
        while (current != null) {
          path.add(new Square(current));
          current = current.parent;
        }
        Collections.reverse(path);
        return path;
      }
      List<Square> adjacentSquares = getAdjacentSquares(currentSquare);
      for (Square square : adjacentSquares) {
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

        square.setParent(currentSquare);
        if (!openList.contains(square)) {
          openList.add(square);
        }
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

  List<Square> getTargetsInRangeForUnit(Unit unit) {
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

    return targetsInRangePositions;
  }

  Optional<Square> getTargetPositionWithShortestPath(Unit unit) {
    List<List<Square>> paths = new ArrayList<>();
    List<Square> targetsInRangePositions =
        getTargetsInRangeForUnit(unit).stream()
            .filter(
                t -> {
                  var path = aStarPathFinding(getSquareForUnit(unit), t);
                  if (path.isEmpty()) {
                    return false;
                  } else {
                    paths.add(path);
                    return true;
                  }
                })
            .sorted(Comparator.comparing(Square::getPosition))
            .collect(Collectors.toCollection(ArrayList::new));
    if (paths.isEmpty()) {
      return Optional.empty();
    }
    int shortestDistance = Integer.MAX_VALUE;
    List<Square> shortestPath = paths.get(0);
    for (List<Square> path : paths) {
      int lastElementDistance = path.get(path.size() - 1).getG();
      if (lastElementDistance < shortestDistance) {
        shortestPath = path;
        shortestDistance = lastElementDistance;
      }
    }

    final int shortest = shortestDistance;
    // need to check if we have more than one path with the same distance to unit
    List<List<Square>> sameDistancePaths =
        paths.stream()
            .filter(path -> path.get(path.size() - 1).getG() == shortest)
            .collect(Collectors.toCollection(ArrayList::new));
    sameDistancePaths.sort(
        (p1, p2) ->
            p1.get(p1.size() - 1).getPosition().compareTo(p2.get(p2.size() - 1).getPosition()));
    // the next position for the unit to move is the first path in the list of paths and
    // the second position from the choose path
    List<Square> winningPath = sameDistancePaths.get(0);
    return Optional.of(winningPath.get(winningPath.size() - 1));
  }

  int getResult1() {
    for (int round = 0; round < 1; round++) {
      // iterate each unit in  reading order, make a copy in order to preserve positions at the
      // begining of the round
      List<Unit> unitsCopy = new ArrayList<>();
      units.stream().forEach(u -> unitsCopy.add(new Unit(u)));
      for (Unit unit : unitsCopy) {
        var targetInRange = getTargetPositionWithShortestPath(unit);
        if (targetInRange.isEmpty()) {
          continue;
        }
//        plotAreaWithRelativeDistance(targetInRange.get());
        var position = getNextMoveForUnit(targetInRange.get(), unit);
        if (position.isEmpty()) {
          continue;
        }
        // update the unit new position
        System.out.println(position);
        units.stream()
            .filter(u -> u.getPosition().equals(unit.getPosition()))
            .forEach(newUnit -> newUnit.setPosition(position.get().getPosition()));
        System.out.println(units);
        // update the unit old position in the map area to '.'
        area.stream()
            .filter(s -> s.getPosition().equals(unit.getPosition()))
            .forEach(s -> s.setSymbol('.'));
        // update the unit new position in the map area to UnitType: goblin or elf
        area.stream()
            .filter(s -> s.getPosition().equals(position.get().getPosition()))
            .forEach(s -> s.setSymbol(getSquareForUnit(unit).getSymbol()));
        displayMap();
      }

      displayMap();
    }
    return -1;
  }

  Optional<Square> getNextMoveForUnit(Square targetInRange, Unit unit) {
    List<Square> adjacentSquares = getAdjacentSquares(getSquareForUnit(unit));
    // if we have more then one path to the target we need to select one that is first in
    // reading order
    List<List<Square>> paths = new ArrayList<>();
    adjacentSquares.stream().forEach(p -> paths.add(aStarPathFinding(p, targetInRange)));
    int minDistance = Integer.MAX_VALUE;
    List<Pair<Square, Integer>> adjacentSquareDistances = new ArrayList<>();
    for (var path : paths) {
      if (path.isEmpty()) {
        continue;
      }
      int distance = path.get(path.size() - 1).getG();
      adjacentSquareDistances.add(new Pair<>(path.get(0), distance));
      minDistance = Integer.min(distance, minDistance);
    }

    return adjacentSquareDistances.stream()
        .sorted(Comparator.comparing(Pair::getValue1))
        .map(p -> p.getValue0())
        .sorted(Comparator.comparing(Square::getPosition))
        .findFirst();
  }
  
  void displayMap() {
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        System.out.print(area.get(i * m + j).getSymbol() + " ");
      }
      System.out.println();
    }
  }
  void plotAreaWithRelativeDistance(Square targetInRange) {
    List<List<Square>> paths = new ArrayList<>();
    dots.stream()
        .filter(d -> !targetInRange.getPosition().equals(d.getPosition()))
        .forEach(d -> paths.add(aStarPathFinding(d, targetInRange)));
    List<Square> newArea = new ArrayList<>();
    area.stream().forEach(s -> newArea.add(new Square(s)));
    for (var path : paths) {
      if (path.isEmpty()) {
        continue;
      }
      Pair<Integer, Integer> first = path.get(0).getPosition();
      Pair<Integer, Integer> last = path.get(path.size() - 1).getPosition();
      int fi = first.getValue0();
      int fj = first.getValue1();
      int li = last.getValue0();
      int lj = last.getValue1();

      newArea.get(m * fi + fj).setSymbol(Character.forDigit(path.get(path.size() - 1).getG(), 10));
      newArea.get(m * li + lj).setSymbol(Character.forDigit(path.get(0).getG(), 10));
    }
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        System.out.print(newArea.get(i * m + j).getSymbol() + " ");
      }
      System.out.println();
    }
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

    public Square(Square from) {
      this.position = from.position;
      this.symbol = from.symbol;
      this.g = from.g;
      this.f = from.f;
      this.h = from.h;
    }

    public void setParent(Square parent) {
      this.parent = parent;
    }

    @Override
    public String toString() {
      return "Square{"
          + "g="
          + g
          + ", h="
          + h
          + ", f="
          + f
          + ", position="
          + position
          + ", symbol="
          + symbol
          + '}';
    }

    public Square(Pair<Integer, Integer> position, char symbol) {
      this.position = position;
      this.symbol = symbol;
      this.g = 0;
      this.f = 0;
      this.h = 0;
      this.parent = null;
    }

    public void resetState() {
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

    public Unit(Unit unit) {
      this.hp = unit.hp;
      this.position = unit.position;
      this.unitType = unit.unitType;
    }

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

    @Override
    public String toString() {
      return "Unit{" + "hp=" + hp + ", position=" + position + ", unitType=" + unitType + '}';
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
