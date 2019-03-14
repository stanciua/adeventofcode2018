import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.javatuples.Pair;

class Day15 {

  private List<Square> area;
  private List<Unit> units;
  private final int columns;

  Day15() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    columns = lines[0].length();
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

  private List<Square> getAdjacentSquares(Square currentSquare, List<Square> area) {
    List<Square> adjacentSquares = new ArrayList<>();
    int i = currentSquare.getPosition().getValue0();
    int j = currentSquare.getPosition().getValue1();
    // up
    Square square = area.get((i - 1) * columns + j);
    if (square.getSymbol() == '.') {
      adjacentSquares.add(square);
    }
    // left
    square = area.get(i * columns + (j - 1));
    if (square.getSymbol() == '.') {
      adjacentSquares.add(square);
    }
    // right
    square = area.get(i * columns + (j + 1));
    if (square.getSymbol() == '.') {
      adjacentSquares.add(square);
    }
    // down
    square = area.get((i + 1) * columns + j);
    if (square.getSymbol() == '.') {
      adjacentSquares.add(square);
    }
    return adjacentSquares;
  }

  private List<Square> pathFinding(Square from, Square to, List<Square> area) {
    List<Square> path = new ArrayList<>();
    List<Square> openList = new ArrayList<>();
    List<Square> closedList = new ArrayList<>();
    openList.add(from);
    area.forEach(Square::resetState);
    while (!openList.isEmpty()) {
      Square currentSquare =
          openList.stream().min(Comparator.comparing(Square::getHeuristic)).get();
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

      List<Square> adjacentSquares = getAdjacentSquares(currentSquare, area);
      for (Square square : adjacentSquares) {
        if (closedList.stream()
            .anyMatch(s -> s.getPosition().equals(square.getPosition()))) {
          continue;
        }

        square.setDistance(currentSquare.getDistance() + 1);
        square.setManhattanDistance(manhattanDistance(square, to));
        square.setHeuristic(square.getDistance() + square.getManhattanDistance());

        Optional<Square> openListSquareOptional =
            openList.stream().filter(s -> s.getPosition().equals(square.getPosition())).findFirst();
        if (openListSquareOptional.isPresent()) {
          Square openListSquare = openListSquareOptional.get();
          int squareG = openListSquare.getDistance();
          if (square.getDistance() > squareG) {
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

  private int manhattanDistance(Square from, Square to) {
    int x1 = from.getPosition().getValue0();
    int y1 = from.getPosition().getValue1();
    int x2 = to.getPosition().getValue0();
    int y2 = to.getPosition().getValue1();

    return Math.abs(x1 - x2) + Math.abs(y1 - y2);
  }

  private Square getSquareForUnit(Unit unit, List<Square> area) {
    return area.stream().filter(s -> s.getPosition().equals(unit.getPosition())).findFirst()
        .orElseThrow();
  }

  private List<Square> getTargetsInRangeForUnit(Unit unit, List<Unit> units, List<Square> area) {
    List<Square> targetsInRangePositions;
    // find targets in range for each type of unit
    if (unit.getUnitType() == UnitType.ELF) {
      targetsInRangePositions =
          units.stream()
              .filter(u -> u.getUnitType() == UnitType.GOBLIN)
              .flatMap(u -> getAdjacentSquares(getSquareForUnit(u, area), area).stream())
              .collect(Collectors.toCollection(ArrayList::new));
    } else {
      targetsInRangePositions =
          units.stream()
              .filter(u -> u.getUnitType() == UnitType.ELF)
              .flatMap(u -> getAdjacentSquares(getSquareForUnit(u, area), area).stream())
              .collect(Collectors.toCollection(ArrayList::new));
    }

    return targetsInRangePositions;
  }

  private Optional<Square> getTargetPositionWithShortestPath(Unit unit, List<Unit> units,
      List<Square> area) {
    List<List<Square>> paths = new ArrayList<>();
    for (var t : getTargetsInRangeForUnit(unit, units, area)) {
      var path = pathFinding(getSquareForUnit(unit, area), t, area);
      if (path.isEmpty()) {
        continue;
      }
      paths.add(path);
    }

    if (paths.isEmpty()) {
      return Optional.empty();
    }
    int shortestDistance = Integer.MAX_VALUE;
    for (List<Square> path : paths) {
      int lastElementDistance = path.get(path.size() - 1).getDistance();
      if (lastElementDistance < shortestDistance) {
        shortestDistance = lastElementDistance;
      }
    }

    final int shortest = shortestDistance;
    // need to check if we have more than one path with the same distance to unit
    List<List<Square>> sameDistancePaths =
        paths.stream()
            .filter(path -> path.get(path.size() - 1).getDistance() == shortest).sorted(
            Comparator.comparing(p -> p.get(p.size() - 1).getPosition()))
            .collect(Collectors.toCollection(ArrayList::new));
    // the next position for the unit to move is the first path in the list of paths and
    // the second position from the choose path
    List<Square> winningPath = sameDistancePaths.get(0);
    return Optional.of(winningPath.get(winningPath.size() - 1));
  }

  private Unit getUnitFromSquare(Square square, List<Unit> units) {
    return units.stream().filter(u -> u.getPosition().equals(square.getPosition())).findFirst()
        .orElseThrow();
  }

  private Optional<Unit> getEnemyToAttack(Unit unit, List<Unit> units, List<Square> area) {
    Square currentSquare = getSquareForUnit(unit, area);
    int i = currentSquare.getPosition().getValue0();
    int j = currentSquare.getPosition().getValue1();
    char enemy = 'G';
    if (unit.getUnitType() == UnitType.GOBLIN) {
      enemy = 'E';
    }
    List<Square> enemies = new ArrayList<>();
    // up
    Square square = area.get((i - 1) * columns + j);
    if (square.getSymbol() == enemy) {
      enemies.add(square);
    }
    // left
    square = area.get(i * columns + (j - 1));
    if (square.getSymbol() == enemy) {
      enemies.add(square);
    }
    // right
    square = area.get(i * columns + (j + 1));
    if (square.getSymbol() == enemy) {
      enemies.add(square);
    }
    // down
    square = area.get((i + 1) * columns + j);
    if (square.getSymbol() == enemy) {
      enemies.add(square);
    }

    int minHP = Integer.MAX_VALUE;
    for (var s : enemies) {
      Unit squareUnit = getUnitFromSquare(s, units);
      minHP = Integer.min(minHP, squareUnit.getHp());
    }
    final int minHpFinal = minHP;
    Optional<Square> target = enemies.stream()
        .filter(e -> getUnitFromSquare(e, units).getHp() == minHpFinal)
        .min(Comparator.comparing(Square::getPosition));
    return target.flatMap(square1 -> Optional.of(getUnitFromSquare(square1, units)));

  }

  private void unitAttack(Unit enemy, Unit attacker, List<Unit> deadUnits, List<Unit> units,
      List<Square> area) {
    enemy.setHp(enemy.getHp() - attacker.getAttackPower());
    if (enemy.getHp() <= 0) {
      deadUnits.add(enemy);
      // we need to destroy the Unit
      units.removeIf(u -> u.getPosition().equals(enemy.getPosition()));
      // update the unit position in the map area to '.'
      area.stream()
          .filter(s -> s.getPosition().equals(enemy.getPosition()))
          .forEach(s -> s.setSymbol('.'));
    }
  }

  private boolean simulateRound(List<Unit> units, List<Square> area, List<Unit> deadUnits) {
    // iterate each unit in  reading order, make a copy in order to preserve positions at the
    // beginning of the round
    List<Unit> unitsCopy = new ArrayList<>();
    units.forEach(u -> unitsCopy.add(new Unit(u)));
    deadUnits.clear();
    for (Unit unit : unitsCopy.stream().sorted(Comparator.comparing(Unit::getPosition))
        .collect(
            Collectors.toCollection(ArrayList::new))) {
      // make sure a dead unit doesn't take a turn, also if one unit dies and another one moves into
      // that position it doesn't get executed again
      if (units.stream().filter(u -> u.getPosition().equals(unit.getPosition())).findFirst()
          .isEmpty()
          || deadUnits.stream().anyMatch(u -> u.getPosition().equals(unit.getPosition()))) {
        continue;
      } // The combat is over when there's no enemy around
      UnitType enemyUnitType;
      if (unit.getUnitType() == UnitType.GOBLIN) {
        enemyUnitType = UnitType.ELF;
      } else {
        enemyUnitType = UnitType.GOBLIN;
      }

      final UnitType enemyUnitTypeFinal = enemyUnitType;
      if (units.stream().noneMatch(u -> u.getUnitType() == enemyUnitTypeFinal)) {
        return true;
      }
      var enemy = getEnemyToAttack(unit, units, area);
      if (enemy.isPresent()) {
        unitAttack(enemy.get(), unit, deadUnits, units, area);
        continue;
      }
      var targetInRange = getTargetPositionWithShortestPath(unit, units, area);
      if (targetInRange.isEmpty()) {
        continue;
      }
      var position = getNextMoveForUnit(targetInRange.get(), unit, area);
      if (position.isEmpty()) {
        continue;
      }
      // update the unit new position
      units.stream()
          .filter(u -> u.getPosition().equals(unit.getPosition()))
          .forEach(newUnit -> newUnit.setPosition(position.get().getPosition()));
      // update the unit new position in the map area to UnitType: goblin or elf
      area.stream()
          .filter(s -> s.getPosition().equals(position.get().getPosition()))
          .forEach(s -> s.setSymbol(getSquareForUnit(unit, area).getSymbol()));
      // update the unit old position in the map area to '.'
      area.stream()
          .filter(s -> s.getPosition().equals(unit.getPosition()))
          .forEach(s -> s.setSymbol('.'));

      Unit currentUnit = units.stream()
          .filter(u -> u.getPosition().equals(position.get().getPosition())).findFirst().orElseThrow();
      Optional<Unit> targetUnit = getEnemyToAttack(currentUnit, units, area);
      targetUnit.ifPresent(unit1 -> unitAttack(unit1, currentUnit, deadUnits, units, area));
    }
    return false;
  }

  private int simulateBattle(List<Unit> units, List<Square> area) {
    int round = 0;
    List<Unit> deadUnits = new ArrayList<>();
    while (true) {
      if (!simulateRound(units, area, deadUnits)) {
        round++;
        continue;
      }
      round++;
      break;
    }
    return
        units.stream().mapToInt(Unit::getHp)
            .reduce(0, (a, x) -> a += x) * (round - 1);
  }

  int getResult1() {
    List<Unit> unitsCopy = units.stream().map(Unit::new)
        .collect(Collectors.toCollection(ArrayList::new));
    List<Square> areaCopy = area.stream().map(Square::new)
        .collect(Collectors.toCollection(ArrayList::new));
    return simulateBattle(unitsCopy, areaCopy);
  }

  private Optional<Square> getNextMoveForUnit(Square targetInRange, Unit unit, List<Square> area) {
    List<Square> adjacentSquares = getAdjacentSquares(getSquareForUnit(unit, area), area);
    // if we have more then one path to the target we need to select one that is first in
    // reading order
    List<List<Square>> paths = new ArrayList<>();
    adjacentSquares.forEach(p -> paths.add(pathFinding(p, targetInRange, area)));
    int minDistance = Integer.MAX_VALUE;
    List<Pair<Square, Integer>> adjacentSquareDistances = new ArrayList<>();
    for (var path : paths) {
      if (path.isEmpty()) {
        continue;
      }
      int distance = path.get(path.size() - 1).getDistance();
      adjacentSquareDistances.add(new Pair<>(path.get(0), distance));
      minDistance = Integer.min(distance, minDistance);
    }
    final int minDistanceFinal = minDistance;
    return adjacentSquareDistances.stream()
        .filter(p -> p.getValue1() == minDistanceFinal)
        .sorted(Comparator.comparing(Pair::getValue1))
        .map(Pair::getValue0).min(Comparator.comparing(Square::getPosition));
  }

  int getResult2() {
    int elfAttackPower = 4;
    long numberOfElfs = units.stream().filter(u -> u.getUnitType() == UnitType.ELF).count();
    int result;
    victoryWithoutCasualties:
    while (true) {
      List<Unit> deadUnits = new ArrayList<>();
      List<Unit> unitsCopy = units.stream().map(Unit::new)
          .collect(Collectors.toCollection(ArrayList::new));
      List<Square> areaCopy = area.stream().map(Square::new)
          .collect(Collectors.toCollection(ArrayList::new));
      final int elfAttackPowerFinal = elfAttackPower;
      unitsCopy.stream().filter(u -> u.getUnitType() == UnitType.ELF)
          .forEach(u -> u.setAttackPower(elfAttackPowerFinal));
      int round = 0;
      while (true) {
        boolean isBattleDone = simulateRound(unitsCopy, areaCopy, deadUnits);
        long currentNumberOfElfs = unitsCopy.stream().filter(u -> u.getUnitType() == UnitType.ELF)
            .count();
        round++;
        if (currentNumberOfElfs != numberOfElfs) {
          elfAttackPower++;
          continue victoryWithoutCasualties;
        }
        if (!isBattleDone) {
          continue;
        }

        result =
            unitsCopy.stream().filter(u -> u.getUnitType() == UnitType.ELF).mapToInt(Unit::getHp)
                .reduce(0, (a, x) -> a += x) * (round - 1);
        break victoryWithoutCasualties;
      }
    }
    return result;
  }

  static class Square {

    int distance;
    Square parent;

    Square(Square from) {
      this.position = from.position;
      this.symbol = from.symbol;
      this.distance = from.distance;
      this.heuristic = from.heuristic;
      this.manhattanDistance = from.manhattanDistance;
      this.parent = from.parent;
    }

    void setParent(Square parent) {
      this.parent = parent;
    }

    @Override
    public String toString() {
      return "Square{"
          + "distance="
          + distance
          + ", manhattanDistance="
          + manhattanDistance
          + ", heuristic="
          + heuristic
          + ", position="
          + position
          + ", symbol="
          + symbol
          + '}';
    }

    Square(Pair<Integer, Integer> position, char symbol) {
      this.position = position;
      this.symbol = symbol;
      this.distance = 0;
      this.heuristic = 0;
      this.manhattanDistance = 0;
      this.parent = null;
    }

    void resetState() {
      this.distance = 0;
      this.heuristic = 0;
      this.manhattanDistance = 0;
      this.parent = null;
    }

    int getDistance() {
      return distance;
    }

    void setDistance(int distance) {
      this.distance = distance;
    }

    int getManhattanDistance() {
      return manhattanDistance;
    }

    void setManhattanDistance(int manhattanDistance) {
      this.manhattanDistance = manhattanDistance;
    }

    int getHeuristic() {
      return heuristic;
    }

    void setHeuristic(int heuristic) {
      this.heuristic = heuristic;
    }

    Pair<Integer, Integer> getPosition() {
      return position;
    }

    int manhattanDistance;

    char getSymbol() {
      return symbol;
    }

    void setSymbol(char symbol) {
      this.symbol = symbol;
    }

    int heuristic;
    final Pair<Integer, Integer> position;
    char symbol;
  }

  static class Unit {

    int hp;

    int getAttackPower() {
      return attackPower;
    }

    void setAttackPower(int attackPower) {
      this.attackPower = attackPower;
    }

    int attackPower;

    Unit(Unit unit) {
      this.hp = unit.hp;
      this.position = unit.position;
      this.unitType = unit.unitType;
      this.attackPower = unit.attackPower;
    }

    int getHp() {
      return hp;
    }

    void setHp(int hp) {
      this.hp = hp;
    }

    Pair<Integer, Integer> getPosition() {
      return position;
    }

    void setPosition(Pair<Integer, Integer> position) {
      this.position = position;
    }

    UnitType getUnitType() {
      return unitType;
    }

    @Override
    public String toString() {
      return "Unit{"
          + "hp=" + hp
          + ", attackPower=" + attackPower
          + ", position=" + position
          + ", unitType=" + unitType
          + '}';
    }

    Unit(Pair<Integer, Integer> position, UnitType unitType) {
      this.position = position;
      this.unitType = unitType;
      this.hp = INITIAL_HP;
      this.attackPower = ATTACK_POWER;
    }

    static final int ATTACK_POWER = 3;
    static final int INITIAL_HP = 200;
    Pair<Integer, Integer> position;
    final UnitType unitType;
  }

  enum UnitType {
    ELF,
    GOBLIN
  }
}
