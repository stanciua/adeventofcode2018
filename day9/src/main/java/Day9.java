import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.OptionalLong;
import java.util.stream.IntStream;

class Day9 {
  private int noOfPlayers;
  private int lastMarble;
  private long[] playerScores;
  private ArrayDeque<Integer> circle;

  Day9(int noOfPlayers, int lastMarble) {
    this.noOfPlayers = noOfPlayers;
    this.lastMarble = lastMarble;
    playerScores = new long[noOfPlayers];
    circle = new ArrayDeque<>();
  }

  private void rotate(ArrayDeque<Integer> deque, int num) {
    if (num == 0) {
      return;
    }

    if (num > 0) {
      IntStream.range(0, num)
          .forEach(
              e -> {
                Integer marble = deque.removeLast();
                deque.addFirst(marble);
              });
    } else {
      IntStream.range(0, Math.abs(num) - 1)
          .forEach(
              e -> {
                Integer marble = deque.remove();
                deque.addLast(marble);
              });
    }
  }

  long getResult1() {
    circle.add(0);
    IntStream.range(1, lastMarble)
        .forEach(
            i -> {
              if (i % 23 == 0) {
                rotate(circle, -7);
                playerScores[i % noOfPlayers] += i + circle.pop();
              } else {
                rotate(circle, 2);
                circle.addLast(i);
              }
            });

    OptionalLong maxScore = Arrays.stream(playerScores).max();
    if (maxScore.isPresent()) {
      return maxScore.getAsLong();
    }
    return 0;
  }

  long getResult2() {
    return getResult1();
  }
}
