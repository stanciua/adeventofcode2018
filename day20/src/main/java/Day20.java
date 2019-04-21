import java.util.HashMap;
import java.util.Map;
import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Day20 {
  char[][] map;
  public Day20() throws Exception {
    String input = Files.readString(Path.of("/Users/stanciua/prog/adeventofcode2018/day20/src/test/java/input.txt"));
    List<Pair<Integer, Integer>> positionsList = new ArrayList<>();
    List<Character> directionList = new ArrayList<>();
    Map<Pair<Integer, Integer>, Character> roomDoorMap = new HashMap<>();
    getMapCoordinates(input, roomDoorMap);
    map = buildMap(roomDoorMap);
    //    displayMap();
  }

  void findLongestPath(
      char[][] map, boolean[][] visited, int i, int j, int x, int y, int[] max_dist, int dist) {
    //    System.out.println("i = " + i + " j = " + j);
    if (i == x && j == y) {
      max_dist[0] = Integer.max(max_dist[0], dist);
      return;
    }

    visited[i][j] = true;

    if (isValid(i + 1, j) && isSafe(map, visited, i + 1, j)) {
      findLongestPath(
          map,
          visited,
          i + 1,
          j,
          x,
          y,
          max_dist,
          map[i][j] == '|' || map[i][j] == '-' ? dist + 1 : dist);
    }
    if (isValid(i, j + 1) && isSafe(map, visited, i, j + 1)) {
      findLongestPath(
          map,
          visited,
          i,
          j + 1,
          x,
          y,
          max_dist,
          map[i][j] == '|' || map[i][j] == '-' ? dist + 1 : dist);
    }
    if (isValid(i - 1, j) && isSafe(map, visited, i - 1, j)) {
      findLongestPath(
          map,
          visited,
          i - 1,
          j,
          x,
          y,
          max_dist,
          map[i][j] == '|' || map[i][j] == '-' ? dist + 1 : dist);
    }
    if (isValid(i, j - 1) && isSafe(map, visited, i, j - 1)) {
      findLongestPath(
          map,
          visited,
          i,
          j - 1,
          x,
          y,
          max_dist,
          map[i][j] == '|' || map[i][j] == '-' ? dist + 1 : dist);
    }

    visited[i][j] = false;
  }

  boolean isSafe(char[][] map, boolean[][] visited, int x, int y) {
    return (map[x][y] == '.' || map[x][y] == '|' || map[x][y] == '-') && !visited[x][y];
  }

  boolean isValid(int x, int y) {
    return x < map.length && y < map[0].length && x >= 0 && y >= 0;
  }

  Pair<Integer, Integer> updateMapAndGetNextPosition(
      Pair<Integer, Integer> currentPosition,
      char direction,
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
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;
    for (Pair<Integer, Integer> position : map.keySet()) {
      maxX = Integer.max(maxX, Math.abs(position.getValue0()));
      maxY = Integer.max(maxY, Math.abs(position.getValue1()));
    }
    int xSize = 2 * maxX + 2;
    int ySize = 2 * maxY + 2;

    char[][] grid = new char[xSize][ySize];
    // first initialize the grid to be all ?
    for (int i = 0; i < xSize; i++) {
      for (int j = 0; j < ySize; j++) {
        grid[i][j] = '?';
      }
    }
    int originX = xSize / 2;
    int originY = ySize / 2;
    grid[originX][originY] = 'X';
    // translate every position in the list by originX/originY and plot them in the grid,
    // based on direction we will plot first the door (- or |) and right after the room (.)
    for (Pair<Integer, Integer> position : map.keySet()) {
      int x = position.getValue0();
      int y = position.getValue1();
      //      System.out.println("x: " + x);
      //      System.out.println("y: " + y);
      //      System.out.println("originX: " + originX);
      //      System.out.println("originY: " + originY);
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

  void getMapCoordinates(String input, Map<Pair<Integer, Integer>, Character> map) {
    Stack<Pair<Integer, Integer>> stack = new Stack<>();
    Pair<Integer, Integer> currentPosition = new Pair<>(0, 0);
    for (char c : input.toCharArray()) {
      if (c == 'E' || c == 'W' || c == 'N' || c == 'S') {
        currentPosition = updateMapAndGetNextPosition(currentPosition, c, map);
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
    // get all the rooms
    List<Pair<Integer, Integer>> roomsCoordinates = new ArrayList<>();
    Pair<Integer, Integer> start = new Pair<>(0, 0);
    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map[0].length; j++) {
        if (map[i][j] == '.') {
          roomsCoordinates.add(new Pair<>(i, j));
        }
        if (map[i][j] == 'X') {
          start = new Pair<>(i, j);
        }
      }
    }
    boolean[][] visited = new boolean[map.length][map[0].length];
    int[] max_dist = {0};
    int max_max_dist = 0;
    for (Pair<Integer, Integer> end : roomsCoordinates) {
      resetVisitedArray(visited);
      findLongestPath(
          map,
          visited,
          start.getValue0(),
          start.getValue1(),
          end.getValue0(),
          end.getValue1(),
          max_dist,
          0);
      max_max_dist = Integer.max(max_max_dist, max_dist[0]);
    }

    return max_max_dist;
  }

  void resetVisitedArray(boolean[][] visited) {
    for (int i = 0; i < visited.length; i++) {
      for (int j = 0; j < visited[0].length; j++) {
        visited[i][j] = false;
      }
    }
  }

  int getResult2() {
    return -1;
  }

  public static void main(String[] args) {
    try {
      Day20 day20 = new Day20();
      System.out.println(day20.getResult1());
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
}
