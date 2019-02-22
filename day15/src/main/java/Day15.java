import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.javatuples.Pair;

class Day15 {

  char[][] area;
  List<Unit> units;

  Day15() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    area = new char[lines.length][];
    IntStream.range(0, lines.length).forEach(i -> area[i] = lines[i].toCharArray());
    units = new ArrayList<>();
    for (int i = 0; i < area.length; i++) {
      for (int j = 0; j < area[0].length; j++) {
        Pair<Integer, Integer> position = new Pair<>(i, j);
        if (area[i][j] == 'G') {
          Unit goblin = new Unit(position, UnitType.GOBLIN);
          units.add(goblin);
        } else if (area[i][j] == 'E') {
          Unit elf = new Unit(position, UnitType.ELF);
          units.add(elf);
        }
      }
    }
  }

  List<Pair<Pair<Integer, Integer>, Integer>> getAdjacentSquares(
      Pair<Pair<Integer, Integer>, Integer> unitPosition) {
    List<Pair<Pair<Integer, Integer>, Integer>> adjacentSquares = new ArrayList<>();
    int i = unitPosition.getValue0().getValue0();
    int j = unitPosition.getValue0().getValue1();
    int counter = unitPosition.getValue1();
    // left
    if (area[i][j - 1] == '.') {
      adjacentSquares.add(new Pair<>(new Pair<>(i, j - 1), counter + 1));
    }
    // right
    if (area[i][j + 1] == '.') {
      adjacentSquares.add(new Pair<>(new Pair<>(i, j + 1), counter + 1));
    }
    // up
    if (area[i - 1][j] == '.') {
      adjacentSquares.add(new Pair<>(new Pair<>(i - 1, j), counter + 1));
    }
    // down
    if (area[i + 1][j] == '.') {
      adjacentSquares.add(new Pair<>(new Pair<>(i + 1, j), counter + 1));
    }

    return adjacentSquares;
  }

  Deque<Pair<Pair<Integer, Integer>, Integer>> pathFinding(Pair<Integer, Integer> from,
      Pair<Integer, Integer> to) {
    Deque<Pair<Pair<Integer, Integer>, Integer>> queue = new LinkedBlockingDeque<>();
    queue.add(new Pair<>(to, 0));
    int count = queue.size();
    while (true) {
      List<Pair<Pair<Integer, Integer>, Integer>> adjacentSquares = getAdjacentSquares(queue.pop());
      List<Pair<Integer, Integer>> toBeRemovedSquares = new ArrayList<>();
      for (var square : adjacentSquares) {
        queue.stream()
            .filter(p -> p.getValue0() == square.getValue0() && p.getValue1() <= square.getValue1())
            .findFirst().ifPresent(s -> toBeRemovedSquares.add(s.getValue0()));
      }

      toBeRemovedSquares.forEach(square -> adjacentSquares.removeIf(p -> p.getValue0() == square));

      queue.addAll(adjacentSquares);
      if (adjacentSquares.stream().filter(square -> square.getValue0() == from).findFirst()
          .isPresent()) {
        break;
      }
    }
    return queue;
  }

  int getResult1() {
    for (int round = 0; round < 1; round++) {
      // iterate each unit in  reading order
      for (Unit unit : units) {
        List<Pair<Pair<Integer, Integer>, Integer>> targetsInRangePositions;
        // find targets in range for each type of unit
        if (unit.getUnitType() == UnitType.ELF) {
          targetsInRangePositions = units.stream()
              .filter(u -> u.getUnitType() == UnitType.GOBLIN)
              .flatMap(u -> getAdjacentSquares(new Pair<>(u.getPosition(), 0)).stream()).collect(
                  Collectors.toCollection(ArrayList::new));
        } else {
          targetsInRangePositions = units.stream()
              .filter(u -> u.getUnitType() == UnitType.ELF)
              .flatMap(u -> getAdjacentSquares(new Pair<>(u.getPosition(), 0)).stream()).collect(
                  Collectors.toCollection(ArrayList::new));
        }
        System.out.println(targetsInRangePositions);
      }
    }
    return -1;
  }

  int getResult2() {
    return -1;
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

    public final static int ATTACK_POWER = 3;
    Pair<Integer, Integer> position;
    UnitType unitType;
  }

  enum UnitType {
    ELF,
    GOBLIN
  }
}
