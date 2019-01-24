import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day12 {

  private List<Character> state;
  private Map<String, Character> rules;
  private int zeroIndex;
  private static final Pattern INITIAL_STATE = Pattern.compile("initial state:\\s(.+)");
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

  private void resizeStateIfNecessary() {
    OptionalInt firstPlantIndex =
        IntStream.range(0, state.size()).filter(i -> state.get(i) == '#').findFirst();

    if (firstPlantIndex.isPresent()) {
      int padPotsLeft = 10 - firstPlantIndex.getAsInt();
      while (padPotsLeft >= 0) {
        state.add(0, '.');
        padPotsLeft--;
        zeroIndex++;
      }
    }

    OptionalInt lastPlantIndex =
        IntStream.range(0, state.size())
            .map(i -> state.size() - i - 1)
            .filter(i -> state.get(i) == '#')
            .findFirst();
    if (lastPlantIndex.isPresent()) {
      int padPotsRigth = 10 - (state.size() - lastPlantIndex.getAsInt());
      while (padPotsRigth >= 0) {
        state.add('.');
        padPotsRigth--;
      }
    }
  }

  private void runGeneration() {
    resizeStateIfNecessary();
    ArrayList<Character> copy = new ArrayList<>(state);
    for (int i = 2; i < state.size() - 2; i++) {
      String llcrr =
          IntStream.range(i - 2, i + 2 + 1)
              .mapToObj(idx -> state.get(idx).toString())
              .collect(Collectors.joining());
      char pot = '.';
      if (rules.containsKey(llcrr)) {
        pot = rules.get(llcrr);
      }
      copy.set(i, pot);
    }
    state = copy;
  }

  int getResult1() {
    IntStream.range(0, 20).forEach(i -> runGeneration());
    return sumOfAllPotsWithPlant();
  }

  private int sumOfAllPotsWithPlant() {
    int sumLeftPots =
        IntStream.rangeClosed(1, zeroIndex)
            .filter(i -> state.get(zeroIndex - i) == '#')
            .map(i -> -i)
            .sum();
    int sumRightPots =
        IntStream.range(zeroIndex, state.size())
            .filter(i -> state.get(i) == '#')
            .map(i -> i - zeroIndex)
            .sum();
    return sumLeftPots + sumRightPots;
  }

  long getResult2() {
    IntStream.range(0, 500).forEach(i -> runGeneration());
    long sum500 = sumOfAllPotsWithPlant();
    return (sum500 / 1000L) * 100_000_000L * 1000 + sum500 % 1000L;
  }
}
