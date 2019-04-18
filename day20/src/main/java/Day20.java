import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day20 {
  Day20() throws Exception {
    String input = Files.readString(Path.of("src/test/java/input.txt"));
    System.out.println(getPaths(input));
  }

  Pair<Integer, Integer> getNextPosition(Pair<Integer, Integer> currentPosition, char direction) {
    Pair<Integer, Integer> nextPosition;
    int x = currentPosition.getValue0();
    int y = currentPosition.getValue1();
    switch (direction) {
      case 'E':
        nextPosition = new Pair<>(x, y + 1);
        break;
      case 'W':
        nextPosition = new Pair<>(x, y - 1);
        break;
      case 'N':
        nextPosition = new Pair<>(x - 1, y);
        break;
      case 'S':
        nextPosition = new Pair<>(x + 1, y);
        break;
      default:
        throw new IllegalArgumentException("Invalid coordinate received");
    }

    return nextPosition;
  }

  List<Pair<Integer, Integer>> getPaths(String input) {
    Stack<Pair<Integer, Integer>> stack = new Stack<>();
    List<Pair<Integer, Integer>> map = new ArrayList<>();
    Pair<Integer, Integer> currentPosition = new Pair<>(0, 0);
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
