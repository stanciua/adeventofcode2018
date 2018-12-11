import com.sun.source.util.Trees;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//Step C must be finished before step A can begin.
class Day7 {
    private Map<Character, Set<Character>> steps = new HashMap<>();
    private Map<Character, Set<Character>> ancestors = new HashMap<>();
    private final static Pattern pattern = Pattern.compile("Step ([A-Z]) must be finished before step ([A-Z]) can begin.");

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
                    steps.put(parentStep, Stream.of(childStep).collect(Collectors.toCollection(TreeSet::new)));
                }
            }
        }
        for (char c : steps.values().stream().flatMap(s -> s.stream()).collect(Collectors.toCollection(TreeSet::new))) {
            ancestors.put(c, steps.entrySet().stream().filter(e -> e.getValue().contains(c)).map(e -> e.getKey()).collect(Collectors.toCollection(TreeSet::new)));
        }
    }

    private Optional<Character> getFirstStep() {
        SortedSet<Character> parents = new TreeSet<>(steps.keySet());
        parents.removeAll(steps.values().stream().flatMap(v -> v.stream()).collect(Collectors.toCollection(TreeSet::new)));
        return parents.stream().findFirst();
    }

    int getResult1() {
        char firstStep = getFirstStep().get();
        Set<Character> toProcessSteps = steps.get(firstStep);
        System.out.print(firstStep);
        processSteps(toProcessSteps);
        return -1;
    }

    void processSteps(Set<Character> toProcessSteps) {
        if (toProcessSteps.size() == 0) {
            return;
        }

        char nextStep = toProcessSteps.stream().findFirst().get();
        System.out.print(nextStep);
        toProcessSteps.remove(nextStep);

        Set<Character> nextSteps = steps.get(nextStep);
        boolean allAncestorsProcessed = nextSteps.stream().map(s -> ancestors.get(s)).filter(a -> a.stream().anyMatch(as -> toProcessSteps.contains(as))).count() == 0;
        if (allAncestorsProcessed) {
            toProcessSteps.addAll(nextSteps);
        }
        processSteps(toProcessSteps);
    }

    int getResult2() {
        return -1;
    }
}


