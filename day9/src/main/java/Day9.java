import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalInt;

class Day9 {
  private int noOfPlayers;
  private int lastMarble;
  private int[] playerScores;
  private ArrayList<Integer> circle;
  private int currentMarble;

  Day9(int noOfPlayers, int lastMarble) {
    this.noOfPlayers = noOfPlayers;
    this.lastMarble = lastMarble;
    playerScores = new int[noOfPlayers];
    circle = new ArrayList<>(lastMarble + 1);
  }

  int getResult1() {
    circle.add(0);
    currentMarble = 0;
    int player = 0;
    for (int i = 1; i <= lastMarble; i++) {
      if (i % 23 == 0) {
        playerScores[player] += i;
        int removalPosition;
        if (currentMarble < 7) {
          int index = 7 - currentMarble;
          removalPosition = circle.size() - index;
        } else {
          removalPosition = currentMarble - 7;
        }
        playerScores[player] += circle.get(removalPosition);
        circle.remove(removalPosition);
        currentMarble = removalPosition;
        player = (player + 1) % noOfPlayers;
        continue;
      }
      int insertionIndex = (currentMarble + 1) % circle.size();
      circle.add(insertionIndex + 1, i);
      currentMarble = insertionIndex + 1;
      player = (player + 1) % noOfPlayers;
    }
    OptionalInt maxScore = Arrays.stream(playerScores).max();
    if (maxScore.isPresent()) {
      return maxScore.getAsInt();
    }

    return 0;
  }

  int getResult2() {
    return -1;
  }
}
