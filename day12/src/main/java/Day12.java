import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day12 {

  List<Character> state;
  Map<String, Character> rules;
  int zeroIndex;
  private static final Pattern INITIAL_STATE =
      Pattern.compile("initial state:\\s(.+)");
  private static final Pattern RULE = Pattern.compile("(.+)\\s=>\\s(.)");

  Day12() throws Exception {
    zeroIndex = 0;
    state = new ArrayList<>();
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    rules = new HashMap<>();
    for (String line : lines) {
      Matcher matchInitialState = INITIAL_STATE.matcher(line);
      if (matchInitialState.matches()) {
        matchInitialState.group(1).chars().forEach(c -> state.add((char) c));
      }

      Matcher rule = RULE.matcher(line);
      if (rule.matches()) {
        rules.put(rule.group(1), rule.group(2).charAt(0));
      }
    }
  }

  void resizeStateIfNecessary() {
    int firstPlantIndex = IntStream.range(0, state.size()).filter(i -> state.get(i) == '#')
        .findFirst().getAsInt();
    if (firstPlantIndex < 2) {
      state.add(0, '.');
      zeroIndex++;
    }
    if (firstPlantIndex < 1) {
      state.add(0, '.');
      zeroIndex++;
    }

    int noOfPots = (state.size() - zeroIndex + 2) % 5;
    while (noOfPots != 0) {
      state.add('.');
      noOfPots = (state.size() - zeroIndex + 2) % 5;
    }
  }

  private void runGeneration() {
    resizeStateIfNecessary();
    ArrayList<Character> copy = state.stream().collect(Collectors.toCollection(ArrayList::new));
    for (int i = zeroIndex; i < state.size() - 2; i ++ ) {
      String llcrr = IntStream.range(i -2, i + 2 + 1).mapToObj(idx -> state.get(idx).toString()).collect(
          Collectors.joining());
      char pot = '.';
      if (rules.containsKey(llcrr)) {
        pot = rules.get(llcrr);
      }
      copy.set(i, pot);
    }
    state = copy;
  }

  int getResult1() {
    IntStream.range(0, 20).forEach(i -> {
      runGeneration();
      System.out.print(i + ": ");
      state.stream().forEach(System.out::print);
      System.out.println();
    });
    return -1;
  }

  int getResult2() {
    return -1;
  }
}
