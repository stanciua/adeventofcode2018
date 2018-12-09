import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class Day6 {
    int[][] coordinates;
    int gridWidth = 0;
    int gridHeight = 0;
    Day6() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/java/input.txt")));
        coordinates = input
                .lines()
                .map(s -> s.split(", "))
                .map(arr -> {
                    String tmp = arr[0];
                    arr[0] = arr[1];
                    arr[1] = tmp;
                    return arr;
                })
                .map(arr -> Arrays.stream(arr).mapToInt(e -> Integer.valueOf(e).intValue()).toArray()).toArray(int[][]::new);

        OptionalInt maxX = Arrays.stream(coordinates).mapToInt(a -> a[0]).max();
        OptionalInt maxY = Arrays.stream(coordinates).mapToInt(a -> a[1]).max();

        if (maxX.isPresent() && maxY.isPresent()) {
            gridHeight = maxX.getAsInt() + 1;
            gridWidth = maxY.getAsInt() + 2;
        }
    }

    boolean isPositionInCoordinates(int x, int y) {
        return Arrays.stream(coordinates).filter(arr -> arr[0] == x && arr[1] == y).count() > 0;
    }

    int calculateArea(String[][] gridChars, String ch) {
        int area = 0;
        for (String[] row : gridChars) {
           for (String c: row) {
               if (c.toLowerCase().equals(ch.toLowerCase())) {
                   area++;
               }
           }
        }

        return area;
    }

    String[][] constructGrid(List<int[]> coordinates) {
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
                        grid[i][j] = -2;
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
        equalDistanceCoordinates.stream().forEach(arr -> gridCharsFinal[arr[0]][arr[1]] = ".");

        return gridChars;
    }

    int calculateSizeForCoordinate(String[][] gridChars, List<int[]> coordinates, int cIndex) {
        String ch = "C" + cIndex;
        int x = coordinates.get(cIndex)[0];
        int y = coordinates.get(cIndex)[1];
        // check to see if this coordinate is infinite
        if (gridChars[x][gridChars[x].length - 1].equals(ch.toLowerCase()) ||
                gridChars[x][0].equals(ch.toLowerCase()) ||
                gridChars[0][y].equals(ch.toLowerCase()) ||
                gridChars[gridChars.length - 1][y].equals(ch.toLowerCase())) {
            return 0;
        }
        return calculateArea(gridChars, ch);
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
        ArrayList<int[]> filteredCoordinates = new ArrayList<>();
        outer:
        for (int i = 0; i < gridHeight ; i++) {
            for (int j = 0; j < gridWidth; j++) {
                if (isPositionInCoordinates(i, j)) {
                    continue;
                }
                final int ii = i;
                final int jj = j;
                int sum = Arrays.stream(coordinates).mapToInt(a -> Math.abs(a[0] - ii) + Math.abs(a[1] - jj)).sum();
                if (sum < 32) {
                   filteredCoordinates.add(new int[]{i, j});
                }

            }
        }

        for (int[] c: filteredCoordinates) {

        }

//        filteredCoordinates.stream().forEach(a -> System.out.println(a[0] + " " + a[1]));

        return -1;
    }
}


