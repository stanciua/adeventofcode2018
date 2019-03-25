import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class Day18 {

  char area[][];
  int width;
  int height;

  Day18() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    area = Arrays.stream(lines).map(line -> line.toCharArray()).toArray(char[][]::new);
    height = area.length;
    width = area[0].length;
  }

  void displayArea() {
    for (int i = 0; i < area.length; i++) {
      for (int j = 0; j < area[0].length; j++) {
        System.out.print(area[i][j]);
      }
      System.out.println();
    }
  }

  void getAdjacentAcres(char[] adjacentAcres, char[][] area, int y, int x) {
    Arrays.fill(adjacentAcres, '$');
    // up
    if (y - 1 >= 0) {
      adjacentAcres[0] = area[y - 1][x];
    }
    // down
    if (y + 1 < height) {
      adjacentAcres[1] = area[y + 1][x];
    }
    //left
    if (x - 1 >= 0) {
      adjacentAcres[2] = area[y][x - 1];
    }
    // right
    if (x + 1 < width) {
      adjacentAcres[3] = area[y][x + 1];
    }
    // diagonal upper-left
    if (x - 1 >= 0 && y - 1 >= 0) {
      adjacentAcres[4] = area[y - 1][x - 1];
    }
    // diagonal upper-right
    if (x + 1 < width && y - 1 >= 0) {
      adjacentAcres[5] = area[y - 1][x + 1];
    }
    // diagonal down-left
    if (x - 1 >= 0 && y + 1 < height) {
      adjacentAcres[6] = area[y + 1][x - 1];
    }
    // diagonal down-right
    if (x + 1 < width && y + 1 < height) {
      adjacentAcres[7] = area[y + 1][x + 1];
    }
  }

  int getResult1() {
    char[] adjacentAcres = new char[8];
    for (int minute = 0; minute < 10; minute++) {
      char[][] areaCopy = Arrays.stream(area).map(a -> a.clone()).toArray(char[][]::new);
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          getAdjacentAcres(adjacentAcres, area, i, j);
          if (area[i][j] == '.') {
            int count = 0;
            for (int idx = 0; idx < adjacentAcres.length; idx++) {
              if (adjacentAcres[idx] == '|') {
                count++;
              }
            }

            if (count >= 3) {
              areaCopy[i][j] = '|';
            }
          }
          if (area[i][j] == '|') {
            int count = 0;
            for (int idx = 0; idx < adjacentAcres.length; idx++) {
              if (adjacentAcres[idx] == '#') {
                count++;
              }
            }

            if (count >= 3) {
              areaCopy[i][j] = '#';
            }
          }
          if (area[i][j] == '#') {
            int countLumberyards = 0;
            int countWoodenAcres = 0;
            for (int idx = 0; idx < adjacentAcres.length; idx++) {
              if (adjacentAcres[idx] == '#') {
                countLumberyards++;
              } else if (adjacentAcres[idx] == '|') {
                countWoodenAcres++;
              }
            }
            if (countLumberyards >= 1 &&
                countWoodenAcres >= 1) {
              areaCopy[i][j] = '#';
            } else {
              areaCopy[i][j] = '.';
            }
          }
        }
      }
      area = areaCopy;
    }
    int lumberyards = 0;
    int woodedAcres = 0;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (area[i][j] == '#') {
          lumberyards++;
        }
        if (area[i][j] == '|') {
          woodedAcres++;
        }
      }
    }
    return lumberyards * woodedAcres;
  }

  int getResult2() {
//    for (int minute = 0; minute < 100_000; minute++) {
//
//      char[][] areaCopy = Arrays.stream(area).map(a -> a.clone()).toArray(char[][]::new);
//      for (int i = 0; i < height; i++) {
//        for (int j = 0; j < width; j++) {
//          if (area[i][j] == '.') {
//            if (getAdjacentAcres(area, i, j).stream().filter(a -> a == '|').count() >= 3) {
//              areaCopy[i][j] = '|';
//            }
//          }
//          if (area[i][j] == '|') {
//            if (getAdjacentAcres(area, i, j).stream().filter(a -> a == '#').count() >= 3) {
//              areaCopy[i][j] = '#';
//            }
//          }
//          if (area[i][j] == '#') {
//            if (getAdjacentAcres(area, i, j).stream().filter(a -> a == '#').count() >= 1 &&
//                getAdjacentAcres(area, i, j).stream().filter(a -> a == '|').count() >= 1) {
//              areaCopy[i][j] = '#';
//            } else {
//              areaCopy[i][j] = '.';
//            }
//          }
//        }
//      }
//      if (areAreasEqual(initialArea, areaCopy)) {
//        System.out.println(minute);
//      }
//      area = areaCopy;
//    }
//    int lumberyards = 0;
//    int woodedAcres = 0;
//    for (int i = 0; i < height; i++) {
//      for (int j = 0; j < width; j++) {
//        if (area[i][j] == '#') {
//          lumberyards++;
//        }
//        if (area[i][j] == '|') {
//          woodedAcres++;
//        }
//      }
//    }
//    return lumberyards * woodedAcres;
    return -1;
  }

  static boolean areAreasEqual(char[][] initialArea, char[][] currentArea) {
    for (int i = 0; i < initialArea.length; i++) {
      for (int j = 0; j < initialArea[0].length; j++) {
        if (initialArea[i][j] != currentArea[i][j]) {
          return false;
        }
      }
    }
    return true;
  }
}

