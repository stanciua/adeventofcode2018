import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;

class Day11 {
  private int grid[][];
  private int gridSerialNumber;
  Day11() throws Exception {
     grid = new int[5][5];
     gridSerialNumber = 18;
  }

  private int getCellPowerLevel(int x, int y, int serialNumber) {
      final int rackId = x + 10;
      int powerLevel = rackId * y + serialNumber;
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
             grid[i][j] = getCellPowerLevel(j + 1, i + 1, gridSerialNumber);
         }
     }
  }

  Pair<Integer, Integer> findTopLeftFuelCell() {
      int max = 0;
      int xleft = 0;
      int yleft = 0;
     for (int i = 0; i < grid.length - 2; i++) {
         for (int j = 0; j < grid[0].length - 2; j++) {
            xleft = j;
            yleft = i;
            
         }
     }
  }


    int getResult1() {
    updateGridWithPowerLevel();
    return -1;
  }

  int getResult2() {
      return -1;
  }
}
