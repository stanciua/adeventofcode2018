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
    private final static Pattern pattern = Pattern.compile("Step ([A-Z]) must be finished before step ([A-Z]) can begin.");
    Day7() throws Exception {
        String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);

        for (String line: lines) {
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
        System.out.println(steps);
    }

    private Optional<Character> getFirstStep() {
        SortedSet<Character> parents = new TreeSet<>(steps.keySet());
        parents.removeAll(steps.values().stream().flatMap(v -> v.stream()).collect(Collectors.toCollection(TreeSet::new)));
        return parents.stream().findFirst();
    }

    int getResult1() {
        char firstStep = getFirstStep().get();
        Set<Character> toProcessSteps = steps.get(firstStep);
        return -1;
    }

    void processSteps(Set<Character> toProcessSteps) {
       if (toProcessSteps.size() == 0) {
           return;
       }


    }

    int getResult2() {
        return -1;
    }
}


