import javax.swing.text.html.Option;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
                    steps.put(parentStep, Stream.of(childStep).collect(Collectors.toCollection(HashSet::new)));
                }
            }
        }
    }

    private Optional<Character> getFirstStep() {
        Set<Character> parents = new HashSet<>(steps.keySet());
        parents.removeAll(steps.values().stream().flatMap(v -> v.stream()).collect(Collectors.toCollection(HashSet::new)));
        return parents.stream().findFirst();
    }

    int getResult1() {
        System.out.println(getFirstStep().get());
        return -1;
    }

    int getResult2() {
        return -1;
    }
}


