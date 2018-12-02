import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day2 {
    private String[] boxes;

    Day2(String inputFile) throws Exception {
        String input = new String(Files.readAllBytes(Paths.get(inputFile)));
        boxes = input.lines().toArray(String[]::new);
    }

    int getResult1() {
        int countTwo = 0;
        int countThree = 0;
        Map<Character, Integer> letterMap = new HashMap<>();
        for (String box : boxes) {
            for (char c : box.toCharArray()) {
                if (letterMap.containsKey(c)) {
                    letterMap.put(c, letterMap.get(c) + 1);
                } else {
                    letterMap.put(c, 1);
                }
            }
            countTwo += letterMap.entrySet().stream().anyMatch(e -> e.getValue() == 2)?1:0;
            countThree += letterMap.entrySet().stream().anyMatch(e -> e.getValue() == 3)?1:0;
            letterMap.clear();
        }
        return countThree * countTwo;
    }

    private Optional<Integer> getDifferentCharPosition(String box1, String box2) {
        int differences = 0;
        int position = 0;
        for (int i = 0; i < box1.length(); i++) {
            if (box1.charAt(i) != box2.charAt(i)) {
                position = i;
                differences++;
            }
            if (differences > 1) {
                return Optional.empty();
            }
        }

        return Optional.of(position);
    }

    String getResult2() {
        String output = "";
        for (int i = 0; i < this.boxes.length; i++) {
            for (int j = i + 1; j < this.boxes.length - 1; j++) {
                if (getDifferentCharPosition(boxes[i], boxes[j]).isPresent()) {
                    int pos = getDifferentCharPosition(boxes[i], boxes[j]).get();
                    final String box = boxes[i];
                    output = IntStream.range(0, boxes[i].length()).filter(p -> p != pos).mapToObj(box::charAt).map(String::valueOf).collect(Collectors.joining());
                }
            }
        }
        return output;
    }
}
