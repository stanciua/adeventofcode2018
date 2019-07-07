import org.javatuples.Triplet;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day23 {
  List<Nanobot> nanobots;
  private static final Pattern nanoBotPattern =
      Pattern.compile("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)");

  Day23() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    int x = 0;
    int y = 0;
    int z = 0;
    nanobots = new ArrayList<>();
    for (var line : lines) {
      Matcher matcher = nanoBotPattern.matcher(line);
      if (matcher.matches()) {
        Triplet<Long, Long, Long> position =
            new Triplet<>(
                Long.parseLong(matcher.group(1)),
                Long.parseLong(matcher.group(2)),
                Long.parseLong(matcher.group(3)));
        long radius = Long.parseLong(matcher.group(4));
        nanobots.add(new Nanobot(position, radius));
      }
    }
  }

  int getResult1() {
    Nanobot strongestNanobot =
        nanobots.stream().max(Comparator.comparing(Nanobot::getRadius)).orElseThrow();
    long strongestNanobotSignal = strongestNanobot.getRadius();
    int countInRange = 0;
    for (Nanobot nanobot : nanobots) {
      if (getManhattanDistance(strongestNanobot.getPosition(), nanobot.getPosition())
          <= strongestNanobotSignal) {
        countInRange++;
      }
    }

    return countInRange;
  }

  long getManhattanDistance(Triplet<Long, Long, Long> from, Triplet<Long, Long, Long> to) {
    return Math.abs(from.getValue0() - to.getValue0())
        + Math.abs(from.getValue1() - to.getValue1())
        + Math.abs(from.getValue2() - to.getValue2());
  }

  long getManhattanDistance(long x1, long y1, long z1, long x2, long y2, long z2) {
    return Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2);
  }

  long getMaximumCubSize(long max) {
    long output = 1;
    while (output < max) {
      output *= 2;
    }
    return output;
  }

  int getResult2() {
    long max = Long.MIN_VALUE;

    for (Nanobot nanobot : nanobots) {
      Triplet<Long, Long, Long> position = nanobot.getPosition();
      max = Long.max(Math.abs(position.getValue0()), max);
      max = Long.max(Math.abs(position.getValue1()), max);
      max = Long.max(Math.abs(position.getValue2()), max);
    }

    System.out.println(getMaximumCubSize(max * 2));
    return -1;
  }

  static class Nanobot {
    Triplet<Long, Long, Long> position;
    long radius;

    @Override
    public String toString() {
      return "Nanobot{" + "position=" + position + ", radius=" + radius + '}';
    }

    public Triplet<Long, Long, Long> getPosition() {
      return position;
    }

    public long getRadius() {
      return radius;
    }

    public Nanobot(Triplet<Long, Long, Long> position, long radius) {
      this.position = position;
      this.radius = radius;
    }
  }

  static class Box {
    Triplet<Long, Long, Long> boxMin;
    Triplet<Long, Long, Long> boxMax;

    public Box(Triplet<Long, Long, Long> boxMin, Triplet<Long, Long, Long> boxMax) {
      this.boxMin = boxMin;
      this.boxMax = boxMax;
    }

    Triplet<Long, Long, Long> getBoxSize() {
      return substractTwoVectors(boxMax, boxMin);
    }

    static Triplet<Long, Long, Long> addTwoVectors(
        Triplet<Long, Long, Long> v1, Triplet<Long, Long, Long> v2) {
      return new Triplet<>(
          v1.getValue0() + v2.getValue0(),
          v1.getValue1() + v2.getValue1(),
          v1.getValue2() + v2.getValue2());
    }

    static Triplet<Long, Long, Long> substractTwoVectors(
        Triplet<Long, Long, Long> v1, Triplet<Long, Long, Long> v2) {
      return new Triplet<>(
          v1.getValue0() - v2.getValue0(),
          v1.getValue1() - v2.getValue1(),
          v1.getValue2() - v2.getValue2());
    }

    Triplet<Long, Long, Long> halfBoxSize() {
      Triplet<Long, Long, Long> boxSize = getBoxSize();
      return new Triplet<>(
          boxSize.getValue0() / 2, boxSize.getValue1() / 2, boxSize.getValue2() / 2);
    }

    Box getFrontLeftTopBox() {
      var halfBoxSize = halfBoxSize();
      var boxMin =
          addTwoVectors(
              this.boxMin, new Triplet<>(0L, halfBoxSize.getValue1(), halfBoxSize.getValue2()));
      var boxMax = substractTwoVectors(this.boxMax, new Triplet<>(halfBoxSize.getValue0(), 0L, 0L));
      return new Box(boxMin, boxMax);
    }

    Box getFrontLeftBottomBox() {
      var halfBoxSize = halfBoxSize();
      var boxMin = addTwoVectors(this.boxMin, new Triplet<>(0L, 0L, halfBoxSize.getValue2()));
      var boxMax =
          substractTwoVectors(
              this.boxMax, new Triplet<>(halfBoxSize.getValue0(), halfBoxSize.getValue1(), 0L));
      return new Box(boxMin, boxMax);
    }

    Box getFrontRightTopBox() {
      var halfBoxSize = halfBoxSize();
      var boxMin =
          addTwoVectors(
              this.boxMin,
              new Triplet<>(
                  halfBoxSize.getValue0(), halfBoxSize.getValue1(), halfBoxSize.getValue2()));
      var boxMax = substractTwoVectors(this.boxMax, new Triplet<>(0L, 0L, 0L));
      return new Box(boxMin, boxMax);
    }

    Box getFrontRightBottomBox() {
      var halfBoxSize = halfBoxSize();
      var boxMin =
          addTwoVectors(
              this.boxMin, new Triplet<>(halfBoxSize.getValue1(), 0L, halfBoxSize.getValue2()));
      var boxMax = substractTwoVectors(this.boxMax, new Triplet<>(0L, halfBoxSize.getValue1(), 0L));
      return new Box(boxMin, boxMax);
    }

    Box getBackLeftTopBox() {
      var halfBoxSize = halfBoxSize();
      var boxMin = addTwoVectors(this.boxMin, new Triplet<>(0L, halfBoxSize.getValue1(), 0L));
      var boxMax =
          substractTwoVectors(
              this.boxMax, new Triplet<>(halfBoxSize.getValue0(), 0L, halfBoxSize.getValue2()));
      return new Box(boxMin, boxMax);
    }

    Box getBackLeftBottomBox() {
      var halfBoxSize = halfBoxSize();
      var boxMin = addTwoVectors(this.boxMin, new Triplet<>(0L, 0L, 0L));
      var boxMax =
          substractTwoVectors(
              this.boxMax,
              new Triplet<>(
                  halfBoxSize.getValue0(), halfBoxSize.getValue1(), halfBoxSize.getValue2()));
      return new Box(boxMin, boxMax);
    }

    Box getBackRightTopBox() {
      var halfBoxSize = halfBoxSize();
      var boxMin =
          addTwoVectors(
              this.boxMin, new Triplet<>(halfBoxSize.getValue0(), halfBoxSize.getValue1(), 0L));
      var boxMax = substractTwoVectors(this.boxMax, new Triplet<>(0L, 0L, halfBoxSize.getValue2()));
      return new Box(boxMin, boxMax);
    }

    Box getBackRightBottomBox() {
      var halfBoxSize = halfBoxSize();
      var boxMin = addTwoVectors(this.boxMin, new Triplet<>(halfBoxSize.getValue0(), 0L, 0L));
      var boxMax =
          substractTwoVectors(
              this.boxMax, new Triplet<>(0L, halfBoxSize.getValue1(), halfBoxSize.getValue2()));
      return new Box(boxMin, boxMax);
    }

    boolean containsPoint(Triplet<Long, Long, Long> point) {
      return boxMin.getValue0() <= point.getValue0()
          && point.getValue0() <= boxMax.getValue0()
          && boxMin.getValue1() <= point.getValue1()
          && point.getValue1() <= boxMax.getValue1()
          && boxMin.getValue2() <= point.getValue2()
          && point.getValue2() <= boxMax.getValue2();
    }

    boolean containsBox(Box box) {
      return this.boxMin.getValue0() <= box.boxMin.getValue0()
          && this.boxMin.getValue1() <= box.boxMin.getValue1()
          && this.boxMin.getValue2() <= box.boxMin.getValue2()
          && this.boxMax.getValue0() >= box.boxMax.getValue0()
          && this.boxMax.getValue1() >= box.boxMax.getValue1()
          && this.boxMax.getValue1() >= box.boxMax.getValue2();
    }

    boolean isContainedInBox(Box box) {
      return this.boxMin.getValue0() >= box.boxMin.getValue0()
          && this.boxMin.getValue1() >= box.boxMin.getValue1()
          && this.boxMin.getValue2() >= box.boxMin.getValue2()
          && this.boxMax.getValue0() <= box.boxMax.getValue0()
          && this.boxMax.getValue1() <= box.boxMax.getValue1()
          && this.boxMax.getValue1() <= box.boxMax.getValue2();
    }

    boolean intersectsBox(Box box) {
      var corners = new ArrayList<Triplet<Long, Long, Long>>();
      // frontLeftTop
      corners.add(new Triplet<>(boxMin.getValue0(), boxMax.getValue1(), boxMax.getValue2()));
      // frontLeftBottom
      corners.add(new Triplet<>(boxMin.getValue0(), boxMin.getValue1(), boxMax.getValue2()));
      // frontRightTop
      corners.add(new Triplet<>(boxMax.getValue0(), boxMax.getValue1(), boxMax.getValue2()));
      // frontRightBottom
      corners.add(new Triplet<>(boxMax.getValue0(), boxMin.getValue1(), boxMax.getValue2()));
      // backLeftTop
      corners.add(new Triplet<>(boxMin.getValue0(), boxMax.getValue1(), boxMin.getValue2()));
      // backLeftBottom
      corners.add(new Triplet<>(boxMin.getValue0(), boxMin.getValue1(), boxMin.getValue2()));
      // backRightTop
      corners.add(new Triplet<>(boxMax.getValue0(), boxMax.getValue1(), boxMin.getValue2()));
      // backRightBottom
      corners.add(new Triplet<>(boxMax.getValue0(), boxMin.getValue1(), boxMin.getValue2()));

      for (var corner : corners) {
        if (box.containsPoint(corner)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public String toString() {
      return "Box from: " + boxMin + " to: " + boxMax;
    }
  }

  static class OctreeNode<T> {
    Box box;
    Triplet<Long, Long, Long> point;
    List<T> elements;
    OctreeNode[] children;
    NodeType type;

    public OctreeNode(Box box) {
      this.box = box;
      type = NodeType.LEAF;
    }

    OctreeNode addElement(T element, Triplet<Long, Long, Long> point) {
      if (!box.containsPoint(point)) {
        return null;
      }

      switch (type) {
        case INTERNAL:
          for (var child : children) {
            if (child.addElement(element, point) != null) {
              return child;
            }
          }
          throw new IllegalArgumentException(
              "box.containsPoint evaluated to true, but none of the children added the point ");

        case LEAF:
          if (this.point != null) {
            if (this.point.equals(point)) {
              this.elements.add(element);
              return this;
            } else {
              return subdivideElementAtPoint(element, point);
            }
          } else {
            if (this.elements == null) {
              this.elements = new ArrayList<>();
            }
            this.elements.add(element);
            this.point = point;
            return this;
          }
        default:
          throw new IllegalArgumentException("Invalid Node Type received");
      }
    }

    OctreeNode subdivideElementAtPoint(T element, Triplet<Long, Long, Long> point) {
      if (this.elements == null) {
        throw new RuntimeException("Subdividing while node doesn't have any elements!");
      }
      if (this.point == null) {
        throw new RuntimeException(("Subdividing while node has no point!"));
      }
      switch (type) {
        case LEAF:
          type = NodeType.INTERNAL;
          children = new OctreeNode[8];
          children[0] = new OctreeNode(this.box.getFrontLeftTopBox());
          children[1] = new OctreeNode(this.box.getFrontLeftBottomBox());
          children[2] = new OctreeNode(this.box.getFrontRightTopBox());
          children[3] = new OctreeNode(this.box.getFrontRightBottomBox());
          children[4] = new OctreeNode(this.box.getBackLeftTopBox());
          children[5] = new OctreeNode(this.box.getBackLeftBottomBox());
          children[6] = new OctreeNode(this.box.getBackRightTopBox());
          children[7] = new OctreeNode(this.box.getBackRightBottomBox());
          addElements(elements, this.point);
          elements = null;
          this.point = null;
          break;
        case INTERNAL:
          throw new RuntimeException("subdividing on an internal node");
      }
      return addElement(element, point);
    }

    void addElements(List<T> elements, Triplet<Long, Long, Long> point) {
      for (var element : elements) {
        addElement(element, point);
      }
    }

    boolean removeElement(T element) {
      switch (type) {
        case LEAF:
          if (!this.elements.isEmpty()) {
            this.elements.remove(element);
            return true;
          }
          return false;
        case INTERNAL:
          for (var child : children) {
            if (child.removeElement(element)) {
              return true;
            }
          }
          return false;
        default:
          throw new IllegalArgumentException("Invalid Node Type received");
      }
    }

    List<T> getElementsAtPoint(Triplet<Long, Long, Long> point) {
      switch (type) {
        case LEAF:
          if (this.point.equals(point)) {
            return elements;
          }
          break;
        case INTERNAL:
          for (var child : children) {
            if (child.box.containsPoint(point)) {
              return child.getElementsAtPoint(point);
            }
          }
          break;
      }
      return null;
    }

    List<T> getElementsInBox(Box box) {
      List<T> elements = new ArrayList<>();
      switch (type) {
        case LEAF:
          if (this.point != null) {
            if (box.containsPoint(point)) {
              elements.addAll(this.elements);
            }
          }
          break;

        case INTERNAL:
          for (var child : children) {
            if (child.box.isContainedInBox(box)) {
              elements.addAll(getElementsInBox(box));
            } else if (child.box.isContainedInBox(box) || child.box.intersectsBox(box)) {
              elements.addAll(child.getElementsInBox(box));
            }
          }
      }
      if (elements.isEmpty()) {
        return null;
      }
      return elements;
    }

    @Override
    public String toString() {
      switch (type) {
        case LEAF:
          return "leaf node with " + box + " elements: " + elements;
        case INTERNAL:
          return "internal node with " + box;
        default:
          throw new IllegalArgumentException("Invalid node type");
      }
    }

    String recursiveDescription(int withTabCount) {
      var indent = new char[withTabCount];
      Arrays.fill(indent, '\t');
      var result = new String(indent) + this + "\n";
      switch (this.type) {
        case INTERNAL:
          for (var child : children) {
            result += child.recursiveDescription(withTabCount + 1);
          }
          break;
        default:
          break;
      }
      return result;
    }

    enum NodeType {
      LEAF,
      INTERNAL
    }
  }

  static class Octree<T> {
    public Octree(Box boundingBox) {
      root = new OctreeNode(boundingBox);
    }

    OctreeNode add(T element, Triplet<Long, Long, Long> point) {
      return root.addElement(element, point);
    }

    boolean remove(T element, OctreeNode node) {
      return node.removeElement(element);
    }

    boolean remove(T element) {
      return root.removeElement(element);
    }

    List<T> getElementsAtPoint(Triplet<Long, Long, Long> point) {
      return root.getElementsAtPoint(point);
    }

    List<T> getElementInBox(Box box) {
      if (!root.box.isContainedInBox(box)) {
        throw new RuntimeException("box is outside of octree bounds");
      }
      return root.getElementsInBox(box);
    }

    @Override
    public String toString() {
      return "Octree\n" + root.recursiveDescription(0);
    }

    OctreeNode root;
  }
}
