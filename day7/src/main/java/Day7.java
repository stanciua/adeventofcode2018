import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Day7 {

  private Map<Character, Set<Character>> steps = new HashMap<>();
  private Map<Character, Set<Character>> ancestors = new HashMap<>();
  private Set<Character> seenSteps = new TreeSet<>();
  private static final Pattern pattern = Pattern
      .compile("Step ([A-Z]) must be finished before step ([A-Z]) can begin.");

  Day7() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);

    for (String line : lines) {
      Matcher match = pattern.matcher(line);
      if (match.matches()) {
        char parentStep = match.group(1).charAt(0);
        char childStep = match.group(2).charAt(0);

        if (steps.containsKey(parentStep)) {
          steps.get(parentStep).add(childStep);
        } else {
          steps
              .put(parentStep, Stream.of(childStep).collect(Collectors.toCollection(TreeSet::new)));
        }
      }
    }
    for (char c : steps.values().stream().flatMap(Set::stream)
        .collect(Collectors.toCollection(TreeSet::new))) {
      ancestors.put(c,
          steps.entrySet().stream().filter(e -> e.getValue().contains(c)).map(Map.Entry::getKey)
              .collect(Collectors.toCollection(TreeSet::new)));
    }
  }

  private Set<Character> getStartingSteps() {
    Set<Character> parents = new TreeSet<>(steps.keySet());
    parents.removeAll(steps.values().stream().flatMap(Set::stream)
        .collect(Collectors.toCollection(TreeSet::new)));
    return new TreeSet<>(parents);
  }

  String getResult1() {
    Set<Character> startSteps = getStartingSteps();
    StringBuilder output = new StringBuilder();
    processSteps(output, startSteps);
    return output.toString();
  }

  private void processSteps(StringBuilder output, Set<Character> toBeProcessedSteps) {
    if (toBeProcessedSteps.isEmpty()) {
      return;
    }

    for (char c : toBeProcessedSteps) {
      Set<Character> ancestorForStep = ancestors.get(c);
      if (ancestorForStep != null && !seenSteps.containsAll(ancestorForStep)) {
        continue;
      }
      seenSteps.add(c);
      output.append(c);
      Set<Character> children = steps.get(c);
      if (children != null) {
        toBeProcessedSteps.addAll(children);
      }
      Set<Character> copy = new TreeSet<>(toBeProcessedSteps);
      copy.remove(c);
      processSteps(output, copy);
      return;
    }
  }

  int getResult2() {
    return -1;
  }
}


