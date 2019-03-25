import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    List<Character> getAdjacentAcres(char[][] area, int y, int x) {
        List<Character> adjacentAcres = new ArrayList<>();
        // up
        if (y - 1 >= 0) {
            adjacentAcres.add(area[y - 1][x]);
        }
        // down
        if (y + 1 < height) {
            adjacentAcres.add(area[y + 1][x]);
        }
        //left
        if (x - 1 >= 0) {
            adjacentAcres.add(area[y][x - 1]);
        }
        // right
        if (x + 1 < width) {
            adjacentAcres.add(area[y][x + 1]);
        }
        // diagonal upper-left
        if (x - 1 >= 0 && y - 1 >= 0) {
            adjacentAcres.add(area[y - 1][x - 1]);
        }
        // diagonal upper-right
        if (x + 1 < width && y - 1 >= 0) {
            adjacentAcres.add(area[y - 1][x + 1]);
        }
        // diagonal down-left
        if (x - 1 >= 0 && y + 1 < height) {
            adjacentAcres.add(area[y + 1][x - 1]);
        }
        // diagonal down-right
        if (x + 1 < width && y + 1 < height) {
            adjacentAcres.add(area[y + 1][x + 1]);
        }

        return adjacentAcres;
    }

    int getResult1() {
        for (int minute = 0; minute < 10; minute++) {

            char[][] areaCopy = Arrays.stream(area).map(a -> a.clone()).toArray(char[][]::new);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (area[i][j] == '.') {
                        if (getAdjacentAcres(area, i, j).stream().filter(a -> a == '|').count() >= 3) {
                            areaCopy[i][j] = '|';
                        }
                    }
                    if (area[i][j] == '|') {
                        if (getAdjacentAcres(area, i, j).stream().filter(a -> a == '#').count() >= 3) {
                            areaCopy[i][j] = '#';
                        }
                    }
                    if (area[i][j] == '#') {
                        if (getAdjacentAcres(area, i, j).stream().filter(a -> a == '#').count() >= 1 &&
                                getAdjacentAcres(area, i, j).stream().filter(a -> a == '|').count() >= 1) {
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
        return -1;
    }
}
