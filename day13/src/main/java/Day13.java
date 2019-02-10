import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.javatuples.Pair;

class Day13 {

  private char[][] track;
  private List<Cart> carts;

  Day13() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    track = new char[lines.length][];
    carts = new ArrayList<>();
    IntStream.range(0, lines.length).forEach(i -> track[i] = lines[i].toCharArray());
  }

  private void updateInitialCartsPositions() {
    for (int i = 0; i < track.length; i++) {
      for (int j = 0; j < track[0].length; j++) {
        if (track[i][j] == '>') {
          Cart cart = new Cart(new Pair<>(i, j), '-', Direction.RIGHT);
          carts.add(cart);
        } else if (track[i][j] == '<') {
          Cart cart = new Cart(new Pair<>(i, j), '-', Direction.LEFT);
          carts.add(cart);
        } else if (track[i][j] == 'v') {
          Cart cart = new Cart(new Pair<>(i, j), '|', Direction.DOWN);
          carts.add(cart);
        } else if (track[i][j] == '^') {
          Cart cart = new Cart(new Pair<>(i, j), '|', Direction.UP);
          carts.add(cart);
        }
      }
    }
  }

  private static char getKartSymbolForDirection(Direction direction) {
    char symbol;
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
      default:
        throw new UnsupportedOperationException("Invalid direction value");
    }

    return symbol;
  }

  private Pair<Integer, Integer> findCollision(boolean firstCollision) {
    Pair<Integer, Integer> position;
    outer:
    do {
      carts.sort(Comparator.comparing(Cart::getPosition));
      List<Cart> crashedCarts = new ArrayList<>();
      for (Cart cart : carts) {
        if (crashedCarts.contains(cart)) {
          continue;
        }
        int i = cart.getPosition().getValue0();
        int j = cart.getPosition().getValue1();
        char nextSymbol;
        if (cart.getDirection() == Direction.RIGHT) {
          nextSymbol = track[i][j + 1];
          if (nextSymbol == '\\') {
            cart.setDirection(Direction.DOWN);
          } else if (nextSymbol == '/') {
            cart.setDirection(Direction.UP);
          } else if (nextSymbol == '+') {
          cart.setDirection(cart.getDirectionForTurn(cart.getTurn()));
            cart.nextTurn();
          }
          cart.setPosition(new Pair<>(i, j + 1));
          track[i][j + 1] = getKartSymbolForDirection(cart.getDirection());
        } else if (cart.direction == Direction.LEFT) {
          nextSymbol = track[i][j - 1];
          if (nextSymbol == '\\') {
            cart.setDirection(Direction.UP);
          } else if (nextSymbol == '/') {
            cart.setDirection(Direction.DOWN);
          } else if (nextSymbol == '+') {
            cart.setDirection(cart.getDirectionForTurn(cart.getTurn()));
            cart.nextTurn();
          }
          cart.setPosition(new Pair<>(i, j - 1));
          track[i][j - 1] = getKartSymbolForDirection(cart.getDirection());
        } else if (cart.getDirection() == Direction.UP) {
          nextSymbol = track[i - 1][j];
          if (nextSymbol == '\\') {
            cart.setDirection(Direction.LEFT);
          } else if (nextSymbol == '/') {
            cart.setDirection(Direction.RIGHT);
          } else if (nextSymbol == '+') {
            cart.setDirection(cart.getDirectionForTurn(cart.getTurn()));
            cart.nextTurn();
          }
          cart.setPosition(new Pair<>(i - 1, j));
          track[i - 1][j] = getKartSymbolForDirection(cart.direction);
        } else {
          nextSymbol = track[i + 1][j];
          if (nextSymbol == '\\') {
            cart.setDirection(Direction.RIGHT);
          } else if (nextSymbol == '/') {
            cart.setDirection(Direction.LEFT);
          } else if (nextSymbol == '+') {
            cart.setDirection(cart.getDirectionForTurn(cart.getTurn()));
            cart.nextTurn();
          }
          cart.setPosition(new Pair<>(i + 1, j));
          track[i + 1][j] = getKartSymbolForDirection(cart.direction);
        }
        track[i][j] = cart.getCurrentSymbol();
        if (nextSymbol != 'v' && nextSymbol != '^' && nextSymbol != '<' && nextSymbol != '>') {
          cart.setCurrentSymbol(nextSymbol);
        }
        List<Cart> collisionCarts =
            carts.stream()
                .filter(k -> k.getPosition().equals(cart.getPosition()))
                .collect(Collectors.toCollection(ArrayList::new));
        // if we have two carts with the same position in the Cart list that means a collision as
        // occurred
        if (collisionCarts.size() == 2) {
          // when we restore the track symbol when collision has happened, we need to replace with
          // the previous cart
          // symbol which was already there
          position = cart.getPosition();
          int x = position.getValue0();
          int y = position.getValue1();
          Optional<Cart> previousCart =
              carts.stream()
                  .filter(
                      k ->
                          k.getPosition().equals(cart.getPosition())
                              && k.getDirection() != cart.getDirection())
                  .findFirst();
          previousCart.ifPresent(crashedCart -> track[x][y] = crashedCart.getCurrentSymbol());
          if (firstCollision) {
            break outer;
          }
          crashedCarts.addAll(collisionCarts);
        }
      }
      carts.removeAll(crashedCarts);
      if (carts.size() == 1) {
        return carts.get(0).getPosition();
      }
    } while (true);
    return position;
  }

  Pair<Integer, Integer> getResult1() {
    updateInitialCartsPositions();
    Pair<Integer, Integer> collisionPosition = findCollision(true);
    return new Pair<>(collisionPosition.getValue1(), collisionPosition.getValue0());
  }

  Pair<Integer, Integer> getResult2() {
    updateInitialCartsPositions();
    Pair<Integer, Integer> lastCartPosition = findCollision(false);
    return new Pair<>(lastCartPosition.getValue1(), lastCartPosition.getValue0());
  }

  static class Cart implements Comparable<Cart> {

    Turn getTurn() {
      return turn;
    }

    Pair<Integer, Integer> position;
    char currentSymbol;

    void setCurrentSymbol(char currentSymbol) {
      this.currentSymbol = currentSymbol;
    }

    Cart(Pair<Integer, Integer> position, char currentSymbol, Direction direction) {
      this.position = position;
      this.currentSymbol = currentSymbol;
      this.direction = direction;
      this.turn = Turn.LEFT;
    }

    void setDirection(Direction direction) {
      this.direction = direction;
    }

    Pair<Integer, Integer> getPosition() {
      return position;
    }

    void setPosition(Pair<Integer, Integer> position) {
      this.position = position;
    }

    char getCurrentSymbol() {
      return currentSymbol;
    }

    Direction getDirection() {
      return direction;
    }

    Direction direction;
    Turn turn;

    @Override
    public String toString() {
      return "Cart { "
          + "x: "
          + position.getValue1()
          + ", y: "
          + position.getValue0()
          + ", direction: "
          + direction
          + ", next_turn: "
          + turn
          + " }";
    }

    @Override
    public int compareTo(Cart o) {
      return this.position.compareTo(o.getPosition());
    }

    Direction getDirectionForTurn(Turn turn) {
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
          default:
            throw new UnsupportedOperationException("Invalid direction value");
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
          default:
            throw new UnsupportedOperationException("Invalid direction value");
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
          default:
            throw new UnsupportedOperationException("Invalid direction value");
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
          default:
            throw new UnsupportedOperationException("Invalid direction value");
        }
      }
      return dir;
    }

    void nextTurn() {
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
    UP,
    LEFT,
    DOWN,
    RIGHT
  }

  enum Turn {
    LEFT,
    RIGHT,
    STRAIGHT
  }
}
