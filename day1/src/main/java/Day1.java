import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class Day1 {
    int[] frequencies;
    public Day1(String inputFile) throws Exception {
        String input = new String(Files.readAllBytes(Paths.get(inputFile)));
        frequencies = input.lines().mapToInt(Integer::valueOf).toArray();
    }

    public int getResult1() {
        return Arrays.stream(this.frequencies).sum();
    }

    public int getResult2() {
        Set<Integer> seenFrequencies = new HashSet<>();
        int currentFreq = 0;
        for (int i = 0;;i++) {
           currentFreq += this.frequencies[i % this.frequencies.length];
           if (seenFrequencies.contains(currentFreq)) {
               return currentFreq;
           }
           seenFrequencies.add(currentFreq);
        }
    }
}
