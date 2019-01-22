import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day12 {

  char[] state;
  Map<String, Character> rules;
  private static final Pattern INITIAL_STATE =
      Pattern.compile("initial state:\\s(.+)");
  private static final Pattern RULE = Pattern.compile("(.+)\\s=>\\s(.)");

  Day12() throws Exception {
    state = new char[0];
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    rules = new HashMap<>();
    for (String line : lines) {
      Matcher matchInitialState = INITIAL_STATE.matcher(line);
      if (matchInitialState.matches()) {
        state = matchInitialState.group(1).toCharArray();
      }

      Matcher rule = RULE.matcher(line);
      if (rule.matches()) {
        rules.put(rule.group(1), rule.group(2).charAt(0));
      }
    }
  }

  int getResult1() {
    return -1;
  }

  int getResult2() {
    return -1;
  }
}
