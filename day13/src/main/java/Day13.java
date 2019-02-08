import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day13 {

  private char[][] track;
  private List<Kart> karts;

  Day13() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    track = new char[lines.length][];
    karts = new ArrayList<>();
    IntStream.range(0, lines.length).forEach(i -> track[i] = lines[i].toCharArray());
  }

  private void updateInitialKartPositions() {
    for (int i = 0; i < track.length; i++) {
      for (int j = 0; j < track[0].length; j++) {
        if (track[i][j] == '>') {
          Kart kart = new Kart(new Pair<>(i, j), '-', Direction.RIGHT, Turn.LEFT);
          karts.add(kart);
        } else if (track[i][j] == '<') {
          Kart kart = new Kart(new Pair<>(i, j), '-', Direction.LEFT, Turn.LEFT);
          karts.add(kart);
        } else if (track[i][j] == 'v') {
          Kart kart = new Kart(new Pair<>(i, j), '|', Direction.DOWN, Turn.LEFT);
          karts.add(kart);
        } else if (track[i][j] == '^') {
          Kart kart = new Kart(new Pair<>(i, j), '|', Direction.UP, Turn.LEFT);
          karts.add(kart);
        }
      }
    }
  }

  static char getKartSymbolForDirection(Direction direction) {
    char symbol = ' ';
    switch (direction) {
      case UP:
        symbol = '^';
        break;
      case DOWN:
        symbol = 'v';
        break;
      case LEFT:
        symbol = '<';
        break;
      case RIGHT:
        symbol = '>';
        break;
    }

    return symbol;
  }

  Pair<Integer, Integer> moveKarts(boolean firstCollision) {
    Pair<Integer, Integer> position;
    long tick = 0;
    outer:
    do {
      karts.sort(Comparator.comparing(Kart::getPosition));
      List<Kart> toBeRemovedKarts = new ArrayList<>();
//      System.out.println("------------------------------------------------");
      for (Kart kart : karts) {
//        System.out.println(kart.getPosition());
        if (toBeRemovedKarts.contains(kart)) {
          continue;
        }
        int i = kart.getPosition().getValue0();
        int j = kart.getPosition().getValue1();
        if (i == 73 && j == 26) {
//          for (char[] l : track) {
//            for (char c : l) {
//              System.out.print(c);
//            }
//            System.out.println();
//          }

        }
//        System.out.println("Coordinate { x: " + kart.getPosition().getValue1() + ", y: " + kart.getPosition().getValue0() + " }");
        if (kart.direction == Direction.RIGHT) {
          char nextSymbol = track[i][j + 1];
          if (nextSymbol == '-') {
            kart.direction = Direction.RIGHT;
          } else if (nextSymbol == '\\') {
            kart.direction = Direction.DOWN;
          } else if (nextSymbol == '/') {
            kart.direction = Direction.UP;
          } else if (nextSymbol == '+') {
            kart.direction = kart.getDirectionForTurn(kart.getTurn());
            kart.nextTurn();
          }
          kart.setPosition(new Pair<>(i, j + 1));
          track[i][j] = kart.currentSymbol;
          if (nextSymbol != 'v' && nextSymbol != '^' && nextSymbol != '<' && nextSymbol != '>') {
            kart.currentSymbol = nextSymbol;
          }
          track[i][j + 1] = getKartSymbolForDirection(kart.direction);
        } else if (kart.direction == Direction.LEFT) {
          char nextSymbol = track[i][j - 1];
          if (nextSymbol == '-') {
            kart.direction = Direction.LEFT;
          } else if (nextSymbol == '\\') {
            kart.direction = Direction.UP;
          } else if (nextSymbol == '/') {
            kart.direction = Direction.DOWN;
          } else if (nextSymbol == '+') {
            kart.direction = kart.getDirectionForTurn(kart.getTurn());
            kart.nextTurn();
          }
          kart.setPosition(new Pair<>(i, j - 1));
          track[i][j] = kart.currentSymbol;
          if (nextSymbol != 'v' && nextSymbol != '^' && nextSymbol != '<' && nextSymbol != '>') {
            kart.currentSymbol = nextSymbol;
          }
          track[i][j - 1] = getKartSymbolForDirection(kart.direction);
        } else if (kart.direction == Direction.UP) {
          char nextSymbol = track[i - 1][j];
          if (nextSymbol == '|') {
            kart.direction = Direction.UP;
          } else if (nextSymbol == '\\') {
            kart.direction = Direction.LEFT;
          } else if (nextSymbol == '/') {
            kart.direction = Direction.RIGHT;
          } else if (nextSymbol == '+') {
            kart.direction = kart.getDirectionForTurn(kart.getTurn());
            kart.nextTurn();
          }
          kart.setPosition(new Pair<>(i - 1, j));
          track[i][j] = kart.currentSymbol;
          if (nextSymbol != 'v' && nextSymbol != '^' && nextSymbol != '<' && nextSymbol != '>') {
            kart.currentSymbol = nextSymbol;
          }
          track[i - 1][j] = getKartSymbolForDirection(kart.direction);
        } else {
          char nextSymbol = track[i + 1][j];
          if (nextSymbol == '|') {
            kart.direction = Direction.DOWN;
          } else if (nextSymbol == '\\') {
            kart.direction = Direction.RIGHT;
          } else if (nextSymbol == '/') {
            kart.direction = Direction.LEFT;
          } else if (nextSymbol == '+') {
            kart.direction = kart.getDirectionForTurn(kart.getTurn());
            kart.nextTurn();
          }
          kart.setPosition(new Pair<>(i + 1, j));
          track[i][j] = kart.currentSymbol;
          if (nextSymbol != 'v' && nextSymbol != '^' && nextSymbol != '<' && nextSymbol != '>') {
            kart.currentSymbol = nextSymbol;
          }
          track[i + 1][j] = getKartSymbolForDirection(kart.direction);
        }
        List<Kart> collisionKarts = karts.stream()
            .filter(k -> k.getPosition().equals(kart.getPosition()))
            .collect(Collectors.toCollection(ArrayList::new));
        if (collisionKarts.size() == 2) {
//          System.out.println("CRASH Coordinate { x: " + kart.getPosition().getValue1() + ", y: " + kart.getPosition().getValue0() + " }");
          position = kart.getPosition();
          int x = position.getValue0();
          int y = position.getValue1();
          System.out.println(y + ", " + x);
          Optional<Kart> previousKart = karts.stream().filter(k -> k.getPosition().equals(kart.getPosition())).skip(1).findFirst();
          if (previousKart.isPresent()) {
            track[x][y] = previousKart.get().getCurrentSymbol();
          }else {
            System.out.println("-----------------FAULT-------------------");
            track[x][y] = kart.getCurrentSymbol();
          }
          if (firstCollision) {
            break outer;
          }
          toBeRemovedKarts.addAll(collisionKarts);
        }
      }
      karts.removeAll(toBeRemovedKarts);
      if (karts.size() == 1) {
        return karts.get(0).getPosition();
      }
      tick++;
    } while (true);
    return position;
  }

  Pair<Integer, Integer> getResult1() {
    updateInitialKartPositions();
    Pair<Integer, Integer> collisionPosition = moveKarts(true);
    Integer x = collisionPosition.getValue0();
    Integer y = collisionPosition.getValue1();
    collisionPosition = collisionPosition.setAt0(y).setAt1(x);
    return collisionPosition;
  }

  Pair<Integer, Integer> getResult2() {
    updateInitialKartPositions();
    moveKarts(false);
//    for (char[] l: track) {
//      for (char c: l) {
//        System.out.print(c);
//      }
//      System.out.println();
//    }
    Pair<Integer, Integer> lastKartPosition = karts.get(0).getPosition();
    Integer x = lastKartPosition.getValue0();
    Integer y = lastKartPosition.getValue1();
    lastKartPosition = lastKartPosition.setAt0(y).setAt1(x);
    return lastKartPosition;
  }

  static class Kart implements Comparable<Kart> {

    public Turn getTurn() {
      return turn;
    }

    public void setTurn(Turn turn) {
      this.turn = turn;
    }

    Pair<Integer, Integer> position;
    char currentSymbol;

    public Kart(
        Pair<Integer, Integer> position, char currentSymbol, Direction direction, Turn turn) {
      this.position = position;
      this.currentSymbol = currentSymbol;
      this.direction = direction;
      this.turn = turn;
    }

    public Pair<Integer, Integer> getPosition() {
      return position;
    }

    public void setPosition(Pair<Integer, Integer> position) {
      this.position = position;
    }

    public char getCurrentSymbol() {
      return currentSymbol;
    }

    public void setCurrentSymbol(char currentSymbol) {
      this.currentSymbol = currentSymbol;
    }

    public Direction getDirection() {
      return direction;
    }

    public void setDirection(Direction direction) {
      this.direction = direction;
    }

    Direction direction;
    Turn turn;

    @Override
    public String toString() {
      return "Kart{"
          + "position="
          + position
          + ", currentSymbol="
          + currentSymbol
          + ", direction="
          + direction
          + ", turn="
          + turn
          + '}';
    }

    @Override
    public int compareTo(Kart o) {
      return this.position.compareTo(o.getPosition());
    }

    public Direction getDirectionForTurn(Turn turn) {
      Direction dir = Direction.DOWN;
      if (this.direction == Direction.RIGHT) {
        switch (turn) {
          case LEFT:
            dir = Direction.UP;
            break;
          case RIGHT:
            dir = Direction.DOWN;
            break;
          case STRAIGHT:
            dir = Direction.RIGHT;
            break;
        }
      } else if (this.direction == Direction.LEFT) {
        switch (turn) {
          case LEFT:
            dir = Direction.DOWN;
            break;
          case RIGHT:
            dir = Direction.UP;
            break;
          case STRAIGHT:
            dir = Direction.LEFT;
            break;
        }
      } else if (this.direction == Direction.UP) {
        switch (turn) {
          case LEFT:
            dir = Direction.LEFT;
            break;
          case RIGHT:
            dir = Direction.RIGHT;
            break;
          case STRAIGHT:
            dir = Direction.UP;
            break;
        }
      } else if (this.direction == Direction.DOWN) {
        switch (turn) {
          case LEFT:
            dir = Direction.RIGHT;
            break;
          case RIGHT:
            dir = Direction.LEFT;
            break;
          case STRAIGHT:
            dir = Direction.DOWN;
            break;
        }
      }
      return dir;
    }

    public void nextTurn() {
      if (this.turn == Turn.LEFT) {
        this.turn = Turn.STRAIGHT;
      } else if (this.turn == Turn.STRAIGHT) {
        this.turn = Turn.RIGHT;
      } else {
        this.turn = Turn.LEFT;
      }
    }
  }

  enum Direction {
    UP(0),
    LEFT(1),
    DOWN(2),
    RIGHT(3);
    private final int direction;

    Direction(int direction) {
      this.direction = direction;
    }
  }

  enum Turn {
    LEFT(0),
    RIGHT(1),
    STRAIGHT(2);
    private final int turn;

    Turn(int turn) {
      this.turn = turn;
    }
  }
}
