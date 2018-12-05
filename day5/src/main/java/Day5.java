import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day5 {

  ArrayList<Character> polymer;

  Day5() throws Exception {
    polymer = (new String(Files.readAllBytes(Paths.get("src/test/java/input.txt")))).chars().map(c -> (Character)c).boxed().collect(Collectors.toCollection(ArrayList::new));
  }

  int getResult1() {
    for (int i = 0; i < polymer.length(); i++) {
      for (int j = i + 1; j < polymer.length(); j++) {
        if (Character.isUpperCase(polymer.charAt(i)) || Character.isUpperCase(polymer.charAt(j))) {

        }
      }
    }
    return -1;
  }

  int getResult2() {
    return -1;
  }
}


