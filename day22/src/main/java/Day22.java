import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

class Day22 {
  static final int DEPTH = 510;
  static final int TARGETY = 10;
  static final int TARGETX = 10;
  static final int MOUTHY = 0;
  static final int MOUTHX = 0;
  static final int SIZEY = 16;
  static final int SIZEX = 16;

  List<Region> regions;
  char[][] map;

  Day22() throws Exception {
    regions = new ArrayList<>();
    initRegions();
  }

  void initRegions() {
    // special cases for MOUTH
    Region mouth = new Region(MOUTHY, MOUTHX);
    mouth.calculateGeologicIndex(regions);
    mouth.calculateErosionLevel(MOUTHY, MOUTHX);
    mouth.setRegionType();
    regions.add(mouth);
    // special cases for TARGET
    Region target = new Region(TARGETY, TARGETX);
    target.calculateGeologicIndex(regions);
    target.calculateErosionLevel(MOUTHY, MOUTHX);
    target.setRegionType();
    regions.add(target);
    // special case for Y == 0
    int y = 0;
    for (int x = MOUTHX + 1; x < SIZEX; x++) {
      Region region = new Region(y, x);
      region.calculateGeologicIndex(regions);
      region.calculateErosionLevel(y, x);
      region.setRegionType();
      regions.add(region);
    }
    // special case for X == 0
    int x = 0;
    for (y = MOUTHY + 1; y < SIZEY; y++) {
      Region region = new Region(y, x);
      region.calculateGeologicIndex(regions);
      region.calculateErosionLevel(y, x);
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
        region.calculateErosionLevel(y, x);
        region.setRegionType();
        regions.add(region);
      }
    }
    regions.sort(Comparator.comparing(Region::getY).thenComparing(Region::getX));
  }

  void displayMap() {
    char[][] map = new char[SIZEY][SIZEX];
    for (Region region : regions) {
      if (region.y == MOUTHY && region.x == MOUTHX) {
        map[region.y][region.x] = 'M';
      } else if (region.y == TARGETY && region.x == TARGETX) {
        map[region.y][region.x] = 'T';
      } else if (region.regionType == RegionType.ROCKY) {
        map[region.y][region.x] = '.';
      } else if (region.regionType == RegionType.WET) {
        map[region.y][region.x] = '=';
      } else if (region.regionType == RegionType.NARROW) {
        map[region.y][region.x] = '|';
      }
    }

    for (int i = 0; i < SIZEY; i++) {
      for (int j = 0; j < SIZEX; j++) {
        System.out.print(map[i][j]);
      }
      System.out.println();
    }
  }

  private List<Region> getAdjacentSquares(Region currentRegion, List<Region> map, List<Region> queue) {
    List<Region> adjacentRegions = new ArrayList<>();
    int i = currentRegion.getY();
    int j = currentRegion.getX();
    // up
    if (i - 1 >= 0) {
      Region region = map.get((i - 1) * SIZEX + j);
      if (queue.contains(region)) {
        adjacentRegions.add(region);
      }
    }
    // left
    if (j - 1 >= 0) {
      Region region = map.get(i * SIZEX + (j - 1));
      if (queue.contains(region)) {
        adjacentRegions.add(region);
      }
    }
    // right
    if (j + 1 < SIZEX) {
      Region region = map.get(i * SIZEX + (j + 1));
      if (queue.contains(region)) {
        adjacentRegions.add(region);
      }
    }
    // down
    if (i + 1 < SIZEY) {
      Region region = map.get((i + 1) * SIZEX + j);
      if (queue.contains(region)) {
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

  int timeToNextRegion(Region currentRegion, Region nextRegion) {
    // .   = rocky    - climbing gear / torch
    // `=` = wet      - climbing gear / neither
    // |   = narrow   - torch / neither
    if (nextRegion.regionType == RegionType.ROCKY) {
      if (currentRegion.tool == Tool.CLIMBING_GEAR || currentRegion.tool == Tool.TORCH) {
        nextRegion.tool = currentRegion.tool;
        return 1;
      } else {
        nextRegion.tool = Tool.CLIMBING_GEAR;
        return 7;
      }
    } else if (nextRegion.regionType == RegionType.WET) {
      if (currentRegion.tool == Tool.CLIMBING_GEAR || currentRegion.tool == Tool.NEITHER) {
        nextRegion.tool = currentRegion.tool;
        return 1;
      } else {
        nextRegion.tool = Tool.NEITHER;
        return 7;
      }
    } else if (nextRegion.regionType == RegionType.NARROW) {
      if (currentRegion.tool == Tool.TORCH || currentRegion.tool == Tool.NEITHER) {
        nextRegion.tool = currentRegion.tool;
        return 1;
      } else {
        nextRegion.tool = Tool.NEITHER;
        return 7;
      }
    }

    throw new IllegalArgumentException("Invalid Region Type -> Tool combination");
  }

  int getLowestTimePath(List<Region> regions, Region source, Region destination) {
    Map<Region, Integer> dist = new HashMap<>();
    List<Region> queue = new ArrayList<>();
    dist.put(source, 0);
    source.tool = Tool.TORCH;
    for (Region region : regions) {
      if (region.getY() != source.getY() || region.getX() != source.getX()) {
        dist.put(region, Integer.MAX_VALUE);
      }
      queue.add(region);
    }

    while (!queue.isEmpty()) {
      Region minTimeRegion =
              queue.stream().min((r1, r2) -> dist.get(r1).compareTo(dist.get(r2))).orElseThrow();
      queue.remove(minTimeRegion);

      System.out.println(minTimeRegion.getY() + ", " + minTimeRegion.getX());
      for (Region region : getAdjacentSquares(minTimeRegion, regions, queue)) {
        int time = dist.get(minTimeRegion) + timeToNextRegion(minTimeRegion, region);
        if (time < dist.get(region)) {
          dist.put(region, time);
        }
        // if we reach the destination region we return the time to get here
        if (destination == region) {
          return dist.get(region);
        }
      }
    }
    return -1;
  }

  static class Region {
    int y;

    public int getY() {
      return y;
    }

    public int getX() {
      return x;
    }

    public Region(int y, int x) {
      this.y = y;
      this.x = x;
    }

    int x;
    int erosionLevel;
    long geologicIndex;
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

    int calculateErosionLevel(int y, int x) {
      this.erosionLevel = (int) ((this.geologicIndex + DEPTH) % 20183);
      return this.erosionLevel;
    }

    @Override
    public String toString() {
      return "Region{" +
              "y=" + y +
              ", x=" + x +
              ", erosionLevel=" + erosionLevel +
              ", geologicIndex=" + geologicIndex +
              ", regionType=" + regionType +
              ", tool=" + tool +
              '}';
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
