import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Day18 {

  private char[][] area;
  private int width;
  private int height;

  Day18() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    area = Arrays.stream(lines).map(String::toCharArray).toArray(char[][]::new);
    height = area.length;
    width = area[0].length;
  }

  private void getAdjacentAcres(char[] adjacentAcres, char[][] area, int y, int x) {
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

  private char[][] getNewAreaAfterIteration(char[] adjacentAcres, char[][] area) {
    char[][] areaCopy = Arrays.stream(area).map(char[]::clone).toArray(char[][]::new);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        getAdjacentAcres(adjacentAcres, area, i, j);
        int count = 0;
        if (area[i][j] == '.') {
          for (char c: adjacentAcres) {
            if (c == '|') {
              count++;
            }
          }

          if (count >= 3) {
            areaCopy[i][j] = '|';
          }
        }
        if (area[i][j] == '|') {
          for (char c : adjacentAcres) {
            if (c == '#') {
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
          for (char c : adjacentAcres) {
            if (c == '#') {
              countLumberyards++;
            } else if (c == '|') {
              countWoodenAcres++;
            }
          }
          if (countLumberyards >= 1
              && countWoodenAcres >= 1) {
            areaCopy[i][j] = '#';
          } else {
            areaCopy[i][j] = '.';
          }
        }
      }
    }
    return areaCopy;
  }

  int getResult1() {
    char[] adjacentAcres = new char[8];
    for (int minute = 0; minute < 10; minute++) {
      area = getNewAreaAfterIteration(adjacentAcres, area);
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
    char[] adjacentAcres = new char[8];
    List<char[][]> seenAreas = new ArrayList<>();
    int minute = 0;
    // we need to find the periodicity to which the area is the same
    List<Integer> equalFrequencies = new ArrayList<>();
    boolean firstMatch = false;
    char[][] matchedArea = null;
    while (minute < 1000) {
      area = getNewAreaAfterIteration(adjacentAcres, area);
      if (!firstMatch && seenAreas.stream().anyMatch(a -> areAreasEqual(a, area))) {
        firstMatch = true;
        matchedArea = Arrays.stream(area).map(char[]::clone).toArray(char[][]::new);
      } else if (firstMatch && areAreasEqual(matchedArea, area)) {
        equalFrequencies.add(minute);
      } else {
        char[][] seen = Arrays.stream(area).map(char[]::clone).toArray(char[][]::new);
        seenAreas.add(seen);
      }
      minute++;
    }
    // we have reached a point where the area repeats itself
    int periodicity = equalFrequencies.get(1) - equalFrequencies.get(0);
    int numberOfIterationTillEnd = (1_000_000_000 - 1000) % periodicity;
    while (numberOfIterationTillEnd > 0) {
      area = getNewAreaAfterIteration(adjacentAcres, area);
      numberOfIterationTillEnd--;
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

  private static boolean areAreasEqual(char[][] initialArea, char[][] currentArea) {
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

