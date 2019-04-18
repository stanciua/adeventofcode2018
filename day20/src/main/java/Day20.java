import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class Day20 {
  char[][] map;
  int originX;
  int originY;
  Day20() throws Exception {
    String input = Files.readString(Path.of("src/test/java/input.txt"));
    map = new char[10][10];
    for (int i =0; i < map.length; i++) {
      for (int j = 0; j <map[0].length; j++) {
        map[i][j] = '?';
      }
    }
    originX = map.length / 2;
    originY = map[0].length / 2;
    map[originX][originY] = 'X';
    
    getPaths(input);
    
    updateMapWithWalls();
    displayMap();
  }
  
  void updateMapWithWalls() {
    for (int i =0; i < map.length; i++) {
      for (int j = 0; j <map[0].length; j++) {
        if (map[i][j] == '?') {
          map[i][j] = '#';
        }
      }
    }
  }
  Pair<Integer, Integer> getNextPosition(Pair<Integer, Integer> currentPosition, char direction) {
    Pair<Integer, Integer> nextPosition;
    int x = currentPosition.getValue0();
    int y = currentPosition.getValue1();
    switch (direction) {
      case 'E':
        nextPosition = new Pair<>(x, y + 2);
        map[x][y+2] = '.';
        map[x][y+1] = '|';
        break;
      case 'W':
        nextPosition = new Pair<>(x, y - 2);
        map[x][y-2] = '.';
        map[x][y-1] = '|';
        break;
      case 'N':
        nextPosition = new Pair<>(x - 2, y);
        map[x-2][y] = '.';
        map[x-1][y] = '-';
        break;
      case 'S':
        nextPosition = new Pair<>(x + 2, y);
        map[x+2][y] = '.';
        map[x+1][y] = '-';
        break;
      default:
        throw new IllegalArgumentException("Invalid coordinate received");
    }
    return nextPosition;
  }
  
  void displayMap() {
    for (int i =0; i < map.length; i++) {
      for (int j = 0; j <map[0].length; j++) {
        System.out.print(map[i][j]);
      }
      System.out.println();
    }
  }
  
  List<Pair<Integer, Integer>> getPaths(String input) {
    Stack<Pair<Integer, Integer>> stack = new Stack<>();
    List<Pair<Integer, Integer>> map = new ArrayList<>();
    Pair<Integer, Integer> currentPosition = new Pair<>(originX, originY);
    Pair<Integer, Integer> nextPosition;
    for (char c : input.toCharArray()) {
      if (c == 'E' || c == 'W' || c == 'N' || c == 'S') {
        nextPosition = getNextPosition(currentPosition, c);
        map.add(nextPosition);
        currentPosition = nextPosition;
      } else if (c == '(') {
        stack.push(map.get(map.size() - 1));
      } else if (c == '|') {
        currentPosition = stack.peek();
      } else if (c == ')') {
        currentPosition = stack.pop();
      }
    }
    return map;
  }

  int getResult1() {
    return -1;
  }

  int getResult2() {
    return -1;
  }
}
