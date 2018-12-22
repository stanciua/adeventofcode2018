import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Day7 {

  private final Map<Character, Set<Character>> steps = new HashMap<>();
  private final Map<Character, Set<Character>> ancestors = new HashMap<>();
  private final Set<Character> seenSteps = new TreeSet<>();
  private static final Pattern pattern =
      Pattern.compile("Step ([A-Z]) must be finished before step ([A-Z]) can begin.");
  private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

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
          steps.put(
              parentStep, Stream.of(childStep).collect(Collectors.toCollection(TreeSet::new)));
        }
      }
    }
    for (char c :
        steps
            .values()
            .stream()
            .flatMap(Set::stream)
            .collect(Collectors.toCollection(TreeSet::new))) {
      ancestors.put(
          c,
          steps
              .entrySet()
              .stream()
              .filter(e -> e.getValue().contains(c))
              .map(Map.Entry::getKey)
              .collect(Collectors.toCollection(TreeSet::new)));
    }
  }

  private Set<Character> getStartingSteps() {
    Set<Character> parents = new TreeSet<>(steps.keySet());
    parents.removeAll(
        steps
            .values()
            .stream()
            .flatMap(Set::stream)
            .collect(Collectors.toCollection(TreeSet::new)));
    return new TreeSet<>(parents);
  }

  String getResult1() {
    Set<Character> startSteps = getStartingSteps();
    StringBuilder output = new StringBuilder();
    processSteps(output, startSteps);
    return output.toString();
  }

  private void processSteps(StringBuilder output, Set<Character> stepsToProcess) {
    outer:
    while (!stepsToProcess.isEmpty()) {
      for (char c : stepsToProcess) {
        Set<Character> ancestorForStep = ancestors.get(c);
        if (ancestorForStep != null && !seenSteps.containsAll(ancestorForStep)) {
          continue;
        }
        seenSteps.add(c);
        output.append(c);
        Set<Character> children = steps.get(c);
        if (children != null) {
          stepsToProcess.addAll(children);
        }
        stepsToProcess.remove(c);
        continue outer;
      }
    }
  }

  private int processSteps2(Set<Character> stepsToProcess) {
    Worker[] workers = IntStream.range(0, 5).mapToObj(i -> new Worker()).toArray(Worker[]::new);
    Set<Character> completedSteps = new TreeSet<>();
    int duration = 0;
    while (!stepsToProcess.isEmpty()) {
      Set<Character> inProgressSteps =
          Arrays.stream(workers)
              .filter(w -> !w.isIdle())
              .map(w1 -> w1.step)
              .collect(Collectors.toCollection(TreeSet::new));
      Set<Character> nextSteps = new TreeSet<>(stepsToProcess);
      nextSteps.removeAll(inProgressSteps);
      Worker[] availableWorkers =
          Arrays.stream(workers).filter(Worker::isIdle).toArray(Worker[]::new);
      for (char step : nextSteps) {
        Set<Character> ancestorForStep = ancestors.get(step);
        if (ancestorForStep != null && !seenSteps.containsAll(ancestorForStep)) {
          continue;
        }

        if (availableWorkers.length != 0) {
          availableWorkers[0].setSteps(ALPHABET.indexOf(step) + 1 + 60, step);
          availableWorkers = Arrays.stream(workers).filter(Worker::isIdle).toArray(Worker[]::new);
        } else {
          break;
        }
      }

      for (Worker worker : workers) {
        worker.execute();
        if (worker.isDone()) {
          completedSteps.add(worker.getStep());
        }
      }
      duration++;

      for (char c : completedSteps) {
        Set<Character> children = steps.get(c);
        seenSteps.add(c);
        if (children != null) {
          stepsToProcess.addAll(children);
        }
        stepsToProcess.remove(c);
      }
      completedSteps.clear();
    }

    return duration;
  }

  int getResult2() {
    Set<Character> startSteps = getStartingSteps();
    return processSteps2(startSteps);
  }

  static class Worker {
    private int noSteps;
    private char step;
    private boolean done = false;

    Worker() {
      this.noSteps = 0;
    }

    boolean isIdle() {
      return noSteps == 0;
    }

    void setSteps(int noSteps, char step) {
      this.done = false;
      this.noSteps = noSteps;
      this.step = step;
    }

    char getStep() {
      return this.step;
    }

    void execute() {
      if (isIdle()) {
        done = false;
      } else {
        noSteps--;
        if (noSteps == 0) {
          done = true;
        }
      }
    }

    boolean isDone() {
      return done;
    }
  }
}
