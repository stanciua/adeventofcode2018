import java.util.*;
import java.util.stream.Collectors;
import org.javatuples.Pair;

class Day22 {
  private static final int DEPTH = 11541;
  private static final int TARGETY = 778;
  private static final int TARGETX = 14;
  private static final int MOUTHY = 0;
  private static final int MOUTHX = 0;
  private static final int SIZEY = 800;
  private static final int SIZEX = 30;
  private final List<Region> regions;

  Day22() {
    regions = new ArrayList<>();
    initRegions();
  }

  private void initRegions() {
    // special cases for MOUTH
    Region mouth = new Region(MOUTHY, MOUTHX);
    mouth.calculateGeologicIndex(regions);
    mouth.calculateErosionLevel();
    mouth.setRegionType();
    regions.add(mouth);
    // special cases for TARGET
    Region target = new Region(TARGETY, TARGETX);
    target.calculateGeologicIndex(regions);
    target.calculateErosionLevel();
    target.setRegionType();
    regions.add(target);
    // special case for Y == 0
    int y = 0;
    for (int x = MOUTHX + 1; x < SIZEX; x++) {
      Region region = new Region(y, x);
      region.calculateGeologicIndex(regions);
      region.calculateErosionLevel();
      region.setRegionType();
      regions.add(region);
    }
    // special case for X == 0
    int x = 0;
    for (y = MOUTHY + 1; y < SIZEY; y++) {
      Region region = new Region(y, x);
      region.calculateGeologicIndex(regions);
      region.calculateErosionLevel();
      region.setRegionType();
      regions.add(region);
    }
    // the rest of the regions
    for (y = MOUTHY + 1; y < SIZEY; y++) {
      for (x = MOUTHX + 1; x < SIZEX; x++) {
        if (y == TARGETY && x == TARGETX) {
          continue;
        }
        Region region = new Region(y, x);
        region.calculateGeologicIndex(regions);
        region.calculateErosionLevel();
        region.setRegionType();
        regions.add(region);
      }
    }
    regions.sort(Comparator.comparing(Region::getY).thenComparing(Region::getX));
  }

  private List<Region> getAdjacentSquares(
      Region currentRegion,
      List<Region> map,
      Map<Pair<Pair<Integer, Integer>, Tool>, Integer> seenCoordinates) {
    List<Region> adjacentRegions = new ArrayList<>();
    int i = currentRegion.getY();
    int j = currentRegion.getX();
    // up
    if (i - 1 >= 0) {
      Region region = map.get((i - 1) * SIZEX + j);
      if (!seenCoordinates.containsKey(new Pair<>(new Pair<>(region.y, region.x), region.tool))) {
        adjacentRegions.add(region);
      }
    }
    // left
    if (j - 1 >= 0) {
      Region region = map.get(i * SIZEX + (j - 1));
      if (!seenCoordinates.containsKey(new Pair<>(new Pair<>(region.y, region.x), region.tool))) {
        adjacentRegions.add(region);
      }
    }
    // right
    if (j + 1 < SIZEX) {
      Region region = map.get(i * SIZEX + (j + 1));
      if (!seenCoordinates.containsKey(new Pair<>(new Pair<>(region.y, region.x), region.tool))) {
        adjacentRegions.add(region);
      }
    }
    // down
    if (i + 1 < SIZEY) {
      Region region = map.get((i + 1) * SIZEX + j);
      if (!seenCoordinates.containsKey(new Pair<>(new Pair<>(region.y, region.x), region.tool))) {
        adjacentRegions.add(region);
      }
    }
    return adjacentRegions;
  }

  int getResult1() {
    int sum = 0;
    List<Region> countedRegions =
        this.regions.stream()
            .filter(r -> r.y >= MOUTHY && r.x >= MOUTHX && r.y <= TARGETY && r.x <= TARGETX)
            .collect(Collectors.toCollection(ArrayList::new));

    for (Region region : countedRegions) {
      switch (region.regionType) {
        case ROCKY:
          sum += 0;
          break;
        case WET:
          sum += 1;
          break;
        case NARROW:
          sum += 2;
          break;
      }
    }
    return sum;
  }

  int getResult2() {
    Region source = regions.get(0);
    Region destination =
        regions.stream()
            .filter(r -> r.getY() == TARGETY && r.getX() == TARGETX)
            .findAny()
            .orElseThrow();
    return getLowestTimePath(regions, source, destination);
  }
  // Part 2 inspired by https://todd.ginsberg.com/post/advent-of-code/2018/day22/
  // Previous solution worked for the simple case example but was off to the races for the provided
  // input
  private int getLowestTimePath(List<Region> regions, Region source, Region destination) {
    Pair<Integer, Integer> fromCoordinate = new Pair<>(source.y, source.x);
    Map<Pair<Pair<Integer, Integer>, Tool>, Integer> seenRegions = new HashMap<>();
    seenRegions.put(new Pair<>(fromCoordinate, source.tool), 0);
    PriorityQueue<Region> regionsToEvaluate =
        new PriorityQueue<>(Comparator.comparing(Region::getCost));
    regionsToEvaluate.add(source);
    source.tool = Tool.TORCH;
    while (!regionsToEvaluate.isEmpty()) {
      var thisPath = regionsToEvaluate.poll();

      if (thisPath.x == destination.x
          && thisPath.y == destination.y
          && thisPath.tool == Tool.TORCH) {
        return thisPath.cost;
      }

      var nextSteps = new ArrayList<Region>();
      for (var neighbor : getAdjacentSquares(thisPath, regions, seenRegions)) {
        if (neighbor.validTools().contains(thisPath.tool)) {
          // we can move forward with this tool, cost will be 1
          var nextStep = new Region(neighbor);
          nextStep.tool = thisPath.tool;
          nextStep.cost = thisPath.cost + 1;
          nextSteps.add(nextStep);
        }
      }

      var remainingTools = new HashSet<>(thisPath.validTools());
      remainingTools.removeAll(Set.of(thisPath.tool));
      for (var tool : remainingTools) {
        // we don't  move in this case, we just switch the tool
        var nextStep = new Region(thisPath);
        nextStep.tool = tool;
        nextStep.cost = thisPath.cost + 7;
        nextSteps.add(nextStep);
      }

      for (var step : nextSteps) {
        var coordinate = new Pair<>(step.y, step.x);
        // the key here is that we can have the same coordinates but with different tool in the
        // map
        var key = new Pair<>(coordinate, step.tool);
        if (!seenRegions.containsKey(key) || seenRegions.get(key) > step.cost) {
          regionsToEvaluate.add(step);
          seenRegions.put(key, step.cost);
        }
      }
    }

    return -1;
  }

  static class Region {
    final int y;

    int getY() {
      return y;
    }

    int getX() {
      return x;
    }

    int getCost() {
      return cost;
    }

    Region(Region other) {
      this.x = other.x;
      this.y = other.y;
      this.erosionLevel = other.erosionLevel;
      this.geologicIndex = other.geologicIndex;
      this.cost = other.cost;
      this.regionType = other.regionType;
      this.tool = other.tool;
    }

    Region(int y, int x) {
      this.y = y;
      this.x = x;
    }

    final int x;
    int erosionLevel;
    long geologicIndex;
    int cost;
    RegionType regionType;
    Tool tool;

    void calculateGeologicIndex(List<Region> regions) {
      if (this.y == 0 && this.x == 0) {
        this.geologicIndex = 0;
      } else if (this.y == TARGETY && this.x == TARGETX) {
        this.geologicIndex = 0;
      } else if (this.y == 0) {
        this.geologicIndex = this.x * 16807;
      } else if (this.x == 0) {
        this.geologicIndex = this.y * 48271;
      } else {
        Region region1 =
            regions.stream().filter(r -> r.y == y && r.x == x - 1).findFirst().orElseThrow();
        Region region2 =
            regions.stream().filter(r -> r.y == y - 1 && r.x == x).findFirst().orElseThrow();
        this.geologicIndex = region1.erosionLevel * region2.erosionLevel;
      }
    }

    void calculateErosionLevel() {
      this.erosionLevel = (int) ((this.geologicIndex + DEPTH) % 20183);
    }

    @Override
    public String toString() {
      return "Region{"
          + "y="
          + y
          + ", x="
          + x
          + ", erosionLevel="
          + erosionLevel
          + ", geologicIndex="
          + geologicIndex
          + ", cost="
          + cost
          + ", regionType="
          + regionType
          + ", tool="
          + tool
          + '}';
    }

    void setRegionType() {
      switch (this.erosionLevel % 3) {
        case 0:
          this.regionType = RegionType.ROCKY;
          break;
        case 1:
          this.regionType = RegionType.WET;
          break;
        case 2:
          this.regionType = RegionType.NARROW;
          break;
        default:
          throw new IllegalArgumentException();
      }
    }

    Set<Tool> validTools() {
      if ((this.x == MOUTHX && this.y == MOUTHY) || (this.x == TARGETX && this.y == TARGETY)) {
        return Set.of(Tool.TORCH);
      } else if (this.regionType == RegionType.ROCKY) {
        return Set.of(Tool.CLIMBING_GEAR, Tool.TORCH);
      } else if (this.regionType == RegionType.WET) {
        return Set.of(Tool.CLIMBING_GEAR, Tool.NEITHER);
      } else if (this.regionType == RegionType.NARROW) {
        return Set.of(Tool.TORCH, Tool.NEITHER);
      }

      return Set.of();
    }
  }

  enum RegionType {
    ROCKY,
    WET,
    NARROW
  }

  enum Tool {
    CLIMBING_GEAR,
    TORCH,
    NEITHER
  }
}
