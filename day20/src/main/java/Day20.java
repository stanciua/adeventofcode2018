import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.IntStream;
import org.javatuples.Pair;

class Day20 {

  private char[][] map;

  public Day20() throws Exception {
    String input = Files.readString(Path.of("src/test/java/input.txt"));
    Map<Pair<Integer, Integer>, Character> roomDoorMap = new HashMap<>();
    getRoomsAndDoorsPositions(input, roomDoorMap);
    map = buildMap(roomDoorMap);
  }

  private void findLongestPath(
      char[][] map, boolean[][] visited, int i, int j, int x, int y, int[] currentDistance,
      int dist) {
    if (i == x && j == y) {
      currentDistance[0] = dist;
      return;
    }

    visited[i][j] = true;

    if (map[i][j] == '|' || map[i][j] == '-') {
      dist++;
    }
    if (canMove(map, visited, i + 1, j)) {
      findLongestPath(map, visited, i + 1, j, x, y, currentDistance, dist);
    }
    if (canMove(map, visited, i, j + 1)) {
      findLongestPath(map, visited, i, j + 1, x, y, currentDistance, dist);
    }
    if (canMove(map, visited, i - 1, j)) {
      findLongestPath(map, visited, i - 1, j, x, y, currentDistance, dist);
    }
    if (canMove(map, visited, i, j - 1)) {
      findLongestPath(map, visited, i, j - 1, x, y, currentDistance, dist);
    }

    visited[i][j] = false;
  }

  private boolean canMove(char[][] map, boolean[][] visited, int x, int y) {
    return x < map.length && y < map[0].length && x >= 0 && y >= 0 && (map[x][y] == '.'
        || map[x][y] == '|' || map[x][y] == '-') && !visited[x][y];
  }

  private Pair<Integer, Integer> updateMapAndGetNextPosition(
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

  private char[][] buildMap(Map<Pair<Integer, Integer>, Character> roomsAndDoorsPositions) {
    // find out the sizes of the grid based on the positions in the map
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;
    for (Pair<Integer, Integer> position : roomsAndDoorsPositions.keySet()) {
      maxX = Integer.max(maxX, Math.abs(position.getValue0()));
      maxY = Integer.max(maxY, Math.abs(position.getValue1()));
    }
    // we size the grid to be the largest X value by largest Y value + 2 to on each coordinate to
    // take the exterior walls into account
    int xSize = 2 * maxX + 2;
    int ySize = 2 * maxY + 2;

    char[][] map = new char[xSize][ySize];
    // first initialize the grid to be all ?
    for (int i = 0; i < xSize; i++) {
      for (int j = 0; j < ySize; j++) {
        map[i][j] = '?';
      }
    }
    int originX = xSize / 2;
    int originY = ySize / 2;
    map[originX][originY] = 'X';
    // translate every position in the list by originX/originY and plot them in the grid,
    // based on direction we will plot first the door (- or |) and right after the room (.)
    for (Pair<Integer, Integer> position : roomsAndDoorsPositions.keySet()) {
      int x = position.getValue0();
      int y = position.getValue1();
      map[originX + x][originY + y] = roomsAndDoorsPositions.get(position);
    }
    // after we've done plotting all the positions, change all unknowns (?) with walls (#)
    for (int i = 0; i < xSize; i++) {
      for (int j = 0; j < ySize; j++) {
        if (map[i][j] == '?') {
          map[i][j] = '#';
        }
      }
    }
    return map;
  }

  private void getRoomsAndDoorsPositions(String input, Map<Pair<Integer, Integer>, Character> map) {
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

  private List<Integer> getListOfDistancesToAllRooms() {
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
    int[] currentDistance = {0};
    int startX = start.getValue0();
    int startY = start.getValue1();
    List<Integer> distances = new ArrayList<>();
    for (Pair<Integer, Integer> end : roomsCoordinates) {
      IntStream.range(0, visited.length)
          .forEach(i -> IntStream.range(0, visited[0].length).forEach(j -> visited[i][j] = false));
      findLongestPath(map, visited, startX, startY, end.getValue0(), end.getValue1(),
          currentDistance, 0);
      distances.add(currentDistance[0]);
    }

    return distances;
  }

  int getResult1() {
    return getListOfDistancesToAllRooms().stream().max(Integer::compareTo).orElseThrow();
  }

  int getResult2() {
    return (int) getListOfDistancesToAllRooms().stream().filter(d -> d >= 1000).count();
  }
}
