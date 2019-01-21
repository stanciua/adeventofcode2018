import org.javatuples.Pair;

class Day11 {

  private final int[][] grid;
  private static final int GRID_SIZE = 300;
  private static final int SERIAL_NUMBER = 1723;

  Day11() {
    grid = new int[GRID_SIZE][GRID_SIZE];
  }

  private int getCellPowerLevel(int x, int y) {
    final int rackId = x + 10;
    int powerLevel = rackId * y + SERIAL_NUMBER;
    powerLevel *= rackId;
    powerLevel %= 1000;
    if (powerLevel < 100) {
      powerLevel = 0;
    } else {
      powerLevel /= 100;
    }
    powerLevel -= 5;
    return powerLevel;
  }

  private void updateGridWithPowerLevel() {
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        grid[i][j] = getCellPowerLevel(j + 1, i + 1);
      }
    }
  }

  private Pair<Pair<Integer, Integer>, Integer> findTopLeftFuelCell(int size) {
    int max = 0;
    int x = 0;
    int y = 0;
    for (int i = 0; i <= grid.length - size; i++) {
      for (int j = 0; j <= grid[0].length - size; j++) {
        int totalPower = getSquareTotalPower(i, j, size);
        if (totalPower > max) {
          max = totalPower;
          x = j + 1;
          y = i + 1;
        }
      }
    }

    return new Pair<>(new Pair<>(x, y), max);
  }

  private int getSquareTotalPower(int i, int j, int size) {
    int total = 0;
    for (int k = 0; k < size; k++) {
      for (int l = 0; l < size; l++) {
        total += grid[i + k][j + l];
      }
    }
    return total;
  }

  Pair<Pair<Integer, Integer>, Integer> getResult1() {
    updateGridWithPowerLevel();
    Pair<Pair<Integer, Integer>, Integer> topLeftAndMax = findTopLeftFuelCell(3);
    Pair<Integer, Integer> topLeft = topLeftAndMax.getValue0();
    return new Pair<>(topLeft, 3);
  }

  Pair<Pair<Integer, Integer>, Integer> getResult2() {
    updateGridWithPowerLevel();
    int maxOfAnySize = 0;
    Pair<Integer, Integer> topLeft = new Pair<>(0, 0);
    int sizeOfMax = 0;
    for (int i = 1; i <= grid.length; i++) {
      Pair<Pair<Integer, Integer>, Integer> topLeftAndMax = findTopLeftFuelCell(i);
      Integer max = topLeftAndMax.getValue1();
      if (max > maxOfAnySize) {
        maxOfAnySize = max;
        topLeft = topLeftAndMax.getValue0();
        sizeOfMax = i;
      }
    }
    return new Pair<>(topLeft, sizeOfMax);
  }
}
