import java.util.HashMap;
import java.util.Map;
import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class Day20 {

  char[][] map;

  Day20() throws Exception {
    String input = Files.readString(Path.of("src/test/java/input.txt"));
    List<Pair<Integer, Integer>> positionsList = new ArrayList<>();
    List<Character> directionList = new ArrayList<>();
    Map<Pair<Integer, Integer>, Character> roomDoorMap = new HashMap<>();
    getPaths(input, roomDoorMap);
    map = buildMap(roomDoorMap);
    displayMap();
  }

  void updateMapWithWalls() {
  }

  Pair<Integer, Integer> nextPosition(Pair<Integer, Integer> currentPosition, char direction,
      Map<Pair<Integer, Integer>, Character> map) {
    Pair<Integer, Integer> nextPosition;
    Pair<Integer, Integer> doorPosition;
    int x = currentPosition.getValue0();
    int y = currentPosition.getValue1();
    switch (direction) {
      case 'E':
        doorPosition = new Pair<>(x, y + 1);
        nextPosition = new Pair<>(x, y + 2);
        map.put(doorPosition, '|');
        break;
      case 'W':
        doorPosition = new Pair<>(x, y - 1);
        nextPosition = new Pair<>(x, y - 2);
        map.put(doorPosition, '|');
        break;
      case 'N':
        doorPosition = new Pair<>(x - 1, y);
        nextPosition = new Pair<>(x - 2, y);
        map.put(doorPosition, '-');
        break;
      case 'S':
        doorPosition = new Pair<>(x + 1, y);
        nextPosition = new Pair<>(x + 2, y);
        map.put(doorPosition, '-');
        break;
      default:
        throw new IllegalArgumentException("Invalid coordinate received");
    }
    map.put(nextPosition, '.');
    return nextPosition;
  }

  void displayMap() {
    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map[0].length; j++) {
        System.out.print(map[i][j]);
      }
      System.out.println();
    }
  }

  char[][] buildMap(Map<Pair<Integer, Integer>, Character> map) {
    // find out the sizes of the grid based on the positions in the map
    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;
    for (Pair<Integer, Integer> position : map.keySet()) {
      minX = Integer.min(minX, position.getValue0());
      minY = Integer.min(minY, position.getValue1());
      maxX = Integer.max(maxX, position.getValue0());
      maxY = Integer.max(maxY, position.getValue1());
    }

    int xSize = Math.abs(minX) + Math.abs(maxX) + 1 + 2;
    int ySize = Math.abs(minY) + Math.abs(maxY) + 1 + 2;

    char[][] grid = new char[xSize][ySize];
    // first initialize the grid to be all ?
    for (int i = 0; i < xSize; i++) {
      for (int j = 0; j < ySize; j++) {
        grid[i][j] = '?';
      }
    }
    int originX = xSize / 2 + 1;
    int originY = ySize / 2 + 1;
    grid[originX][originY] = 'X';
    // translate every position in the list by originX/originY and plot them in the grid,
    // based on direction we will plot first the door (- or |) and right after the room (.)
    for (Pair<Integer, Integer> position : map.keySet()) {
      int x = position.getValue0();
      int y = position.getValue1();
      grid[originX + x][originY + y] = map.get(position);
    }
    // after we've done plotting all the positions, change all unknowns (?) with walls (#)
    for (int i = 0; i < xSize; i++) {
      for (int j = 0; j < ySize; j++) {
        if (grid[i][j] == '?') {
          grid[i][j] = '#';
        }
      }
    }
    return grid;
  }

  void getPaths(String input, Map<Pair<Integer, Integer>, Character> map) {
    Stack<Pair<Integer, Integer>> stack = new Stack<>();
    Pair<Integer, Integer> currentPosition = new Pair<>(0, 0);
    Pair<Integer, Integer> nextPosition;
    for (char c : input.toCharArray()) {
      if (c == 'E' || c == 'W' || c == 'N' || c == 'S') {
        nextPosition = nextPosition(currentPosition, c, map);
        currentPosition = nextPosition;
      } else if (c == '(') {
        stack.push(currentPosition);
      } else if (c == '|') {
        currentPosition = stack.peek();
      } else if (c == ')') {
        currentPosition = stack.pop();
      }
    }
  }

  int getResult1() {
    return -1;
  }

  int getResult2() {
    return -1;
  }
}
