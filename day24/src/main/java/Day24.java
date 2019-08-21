import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.javatuples.Pair;
import org.javatuples.Triplet;

class Day24 {
  private List<Nanobot> nanobots;
  private static final Pattern ImmuneSystemPattern =
      Pattern.compile("Immune System:");
  private static final Pattern InfectionPattern =
          Pattern.compile("Infection:");

  Day24() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
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
    return -1;
  }

  int getResult2() {
    return -1;
  }

  static class System {
    List<Group> groups;
  }

  static class Group {
    public Group(List<Unit> units) {
      this.units = units;
    }

    public int effectivePower() {
      return units.get(0).attackDamage * units.size();
    }

    List<Unit> units;
  }

  static class Unit {
    int hitPoint;
    int attackDamage;
    AttackType attackType;

    public Unit(
        int hitPoint,
        int attackDamage,
        AttackType attackType,
        int initiative,
        List<Weakness> weaknesses,
        List<Immunity> immunities) {
      this.hitPoint = hitPoint;
      this.attackDamage = attackDamage;
      this.attackType = attackType;
      this.initiative = initiative;
      this.weaknesses = weaknesses;
      this.immunities = immunities;
    }

    int initiative;
    List<Weakness> weaknesses;
    List<Immunity> immunities;
  }

  enum Weakness {
    BLUDGEONING,
    COLD,
    FIRE,
    RADIATION,
    SLASHING
  }

  enum Immunity {
    BLUDGEONING,
    COLD,
    FIRE,
    RADIATION,
    SLASHING
  }
}
