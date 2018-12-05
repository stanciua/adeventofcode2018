import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class Day5 {

    private ArrayList<Character> polymer;

    Day5() throws Exception {
        polymer = (new String(Files.readAllBytes(Paths.get("src/test/java/input.txt")))).strip().chars().mapToObj(c -> (char) c).collect(Collectors.toCollection(ArrayList::new));
    }

    private void executeReaction(ArrayList<Character> currentPolymer) {
        for (int i = 0; i < currentPolymer.size() - 1; i++) {
            char c1 = currentPolymer.get(i);
            char c2 = currentPolymer.get(i + 1);
            if ((Character.isUpperCase(c1) && Character.isLowerCase(c2)) ||
                    (Character.isLowerCase(c1) && Character.isUpperCase(c2))) {
                if (Character.toUpperCase(c1) == Character.toUpperCase(c2)) {
                    currentPolymer.remove(i);
                    currentPolymer.remove(i);
                    return;
                }
            }
        }
    }

    private int getPolymerLength(ArrayList<Character> currentPolymer) {
        int currentLength = currentPolymer.size();
        executeReaction(currentPolymer);
        while (currentLength != currentPolymer.size()) {
            currentLength = currentPolymer.size();
            executeReaction(currentPolymer);
        }

        return currentPolymer.size();
    }

    int getResult1() {
        return getPolymerLength(polymer);
    }

    int getResult2() {
        Set<Character> units = polymer.stream().map(Character::toLowerCase).collect(Collectors.toCollection(HashSet::new));
        int shortestPolymerLength = Integer.MAX_VALUE;
        for (char c : units) {
            ArrayList<Character> strippedPolymer = new ArrayList<>(polymer);
            strippedPolymer.removeIf(ch -> c == Character.toLowerCase(ch));
            int polymerLength = getPolymerLength(strippedPolymer);
            if (polymerLength < shortestPolymerLength) {
                shortestPolymerLength = polymerLength;
            }
        }
        return shortestPolymerLength;
    }
}


