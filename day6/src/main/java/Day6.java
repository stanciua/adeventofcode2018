import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

class Day6 {

  private int[][] coordinates;
  private int gridWidth = 0;
  private int gridHeight = 0;

  Day6() throws Exception {
    String input = new String(Files.readAllBytes(Paths.get("src/test/java/input.txt")));
    coordinates = input
        .lines()
        .map(s -> s.split(", "))
        .map(arr -> Arrays.stream(arr).mapToInt(Integer::valueOf).toArray()).toArray(int[][]::new);

    OptionalInt maxX = Arrays.stream(coordinates).mapToInt(a -> a[0]).max();
    OptionalInt maxY = Arrays.stream(coordinates).mapToInt(a -> a[1]).max();

    if (maxX.isPresent() && maxY.isPresent()) {
      // Choose the grid size based on the example shown in part 1, max of X + 1 and max of Y + 2
      gridHeight = maxX.getAsInt() + 1;
      gridWidth = maxY.getAsInt() + 2;
    }
  }

  private boolean isPositionInCoordinates(int x, int y) {
    return Arrays.stream(coordinates).anyMatch(arr -> arr[0] == x && arr[1] == y);
  }

  private int calculateAreaSize(String[][] gridChars, String ch) {
    return (int) Arrays.stream(gridChars).flatMap(Arrays::stream)
        .filter(c -> c.toLowerCase().equals(ch.toLowerCase())).count();
  }

  private String[][] constructGrid(List<int[]> coordinates) {
    int[][] grid = new int[gridHeight][gridWidth];
    String[][] gridChars = new String[gridHeight][gridWidth];

    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        grid[i][j] = Integer.MAX_VALUE;
      }
    }
    ArrayList<int[]> equalDistanceCoordinates = new ArrayList<>();
    for (int c = 0; c < coordinates.size(); c++) {
      int x = coordinates.get(c)[0];
      int y = coordinates.get(c)[1];
      for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[i].length; j++) {
          int distance = Math.abs(x - i) + Math.abs(y - j);
          if (i == x && j == y) {
            gridChars[i][j] = "C" + c;
            continue;
          }
          if (isPositionInCoordinates(i, j)) {
            continue;
          }
          int val = grid[i][j];
          if (distance < val) {
            grid[i][j] = distance;
            gridChars[i][j] = ("C" + c).toLowerCase();
            final int ii = i;
            final int jj = j;
            equalDistanceCoordinates.removeIf(arr -> arr[0] == ii && arr[1] == jj);
          } else if (distance == val) {
            grid[i][j] = distance;
            equalDistanceCoordinates.add(new int[]{i, j});
          }
        }
      }
    }
    final String[][] gridCharsFinal = gridChars;
    equalDistanceCoordinates.forEach(arr -> gridCharsFinal[arr[0]][arr[1]] = ".");
    return gridChars;
  }

  private int calculateSizeForCoordinate(String[][] gridChars, List<int[]> coordinates,
      int index) {
    String ch = "C" + index;
    int x = coordinates.get(index)[0];
    int y = coordinates.get(index)[1];
    // check to see if this coordinate is infinite:
    //   - if any of the extremities have the value of the coordinate we are searching for then this
    //     should be flagged as infinite area
    if (gridChars[x][gridChars[x].length - 1].equals(ch.toLowerCase())
        || gridChars[x][0].equals(ch.toLowerCase())
        || gridChars[0][y].equals(ch.toLowerCase())
        || gridChars[gridChars.length - 1][y].equals(ch.toLowerCase())) {
      return 0;
    }
    return calculateAreaSize(gridChars, ch);
  }

  int getResult1() {
    List<int[]> coordinatesList = Arrays.asList(coordinates);
    String[][] gridChars = constructGrid(coordinatesList);
    int largestArea = 0;
    for (int c = 0; c < coordinates.length; c++) {
      int area = calculateSizeForCoordinate(gridChars, coordinatesList, c);
      largestArea = Integer.max(area, largestArea);
    }

    return largestArea;
  }

  int getResult2() {
    int count = 0;
    for (int i = 0; i < gridHeight; i++) {
      for (int j = 0; j < gridWidth; j++) {
        final int ii = i;
        final int jj = j;
        int sum = Arrays.stream(coordinates)
            .mapToInt(a -> Math.abs(a[0] - ii) + Math.abs(a[1] - jj)).sum();
        if (sum < 10000) {
          count++;
        }
      }
    }
    return count;
  }
}


