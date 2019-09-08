import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.javatuples.Pair;

class Day24 {
  private List<Group> infectionSystem;
  private List<Group> immuneSystem;
  private static final Pattern ImmuneSystemPattern = Pattern.compile("Immune System:");
  private static final Pattern InfectionPattern = Pattern.compile("Infection:");
  private static final Pattern groupPattern =
      Pattern.compile(
          "(\\d+) units each with (\\d+) hit points (\\(.+\\) )?with an attack that does"
              + " (\\d+) (\\w+) damage at initiative (\\d+)");

  Day24() throws Exception {
    infectionSystem = new ArrayList<>();
    immuneSystem = new ArrayList<>();
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    boolean isImmuneSystem = false;
    boolean isInfectionSystem = false;
    int groupId = 1;
    for (var line : lines) {
      Matcher matcher = ImmuneSystemPattern.matcher(line);
      if (matcher.matches()) {
        isImmuneSystem = true;
      }
      matcher = InfectionPattern.matcher(line);
      if (matcher.matches()) {
        isInfectionSystem = true;
        isImmuneSystem = false;
        groupId = 1;
      }

      matcher = groupPattern.matcher(line);
      if (matcher.matches()) {
        int numberOfUnits = Integer.valueOf(matcher.group(1));
        int hitPoints = Integer.valueOf(matcher.group(2));
        String weaknessAndImmunities = matcher.group(3);
        List<AttackType> weaknesses = getWeaknessesOrImmunities(weaknessAndImmunities, "weak");
        List<AttackType> immunities = getWeaknessesOrImmunities(weaknessAndImmunities, "immune");
        int attackDamage = Integer.valueOf(matcher.group(4));
        AttackType attackType = AttackType.from(matcher.group(5));
        int initiative = Integer.valueOf(matcher.group(6));
        BattleSystem battleSystem = BattleSystem.IMMUNE;
        if (isInfectionSystem) {
          battleSystem = BattleSystem.INFECTION;
        }
        Group group =
            new Group(
                groupId++,
                numberOfUnits,
                hitPoints,
                attackDamage,
                attackType,
                initiative,
                battleSystem,
                weaknesses,
                immunities);
        if (isImmuneSystem) {
          immuneSystem.add(group);
        }
        if (isInfectionSystem) {
          infectionSystem.add(group);
        }
      }
    }
  }

  private static List<AttackType> getWeaknessesOrImmunities(
      String weaknessesAndImmunities, String keyword) {
    List<AttackType> output = new ArrayList<>();
    if (weaknessesAndImmunities == null || weaknessesAndImmunities.length() == 0) {
      return output;
    }

    if (!weaknessesAndImmunities.contains(keyword + " to ")) {
      return output;
    }

    int indexOfKeyword =
        weaknessesAndImmunities.indexOf(keyword + " to ") + keyword.length() + " to ".length();
    int indexOfEndKeyword = weaknessesAndImmunities.indexOf(";", indexOfKeyword);
    if (indexOfEndKeyword == -1) {
      indexOfEndKeyword = weaknessesAndImmunities.indexOf(")", indexOfKeyword);
    }

    String result = weaknessesAndImmunities.substring(indexOfKeyword, indexOfEndKeyword);
    Arrays.stream(result.split(","))
        .filter(e -> e.length() != 0)
        .forEach(e -> output.add(AttackType.from(e.strip())));
    return output;
  }

  private List<Pair<Group, Optional<Group>>> targetSelection(
      List<Group> attackers, List<Group> defenders) {
    List<Group> attackersGroupOrder =
        attackers.stream()
            .sorted(
                Comparator.comparing(Group::effectivePower)
                    .thenComparing(Group::getInitiative)
                    .reversed())
            .collect(Collectors.toCollection(ArrayList::new));
    // next we need pair one attacking group to one defending group
    return getAttackAndDefendGroupPair(
        attackersGroupOrder, new ArrayList<>(defenders));
  }

  private boolean attackPhase(
      List<Pair<Group, Optional<Group>>> immune, List<Pair<Group, Optional<Group>>> infection) {
    // we put all the attacks in on place in order to sorted them based on reverse initiative
    var allAttacks = new ArrayList<Pair<Group, Optional<Group>>>();
    allAttacks.addAll(immune);
    allAttacks.addAll(infection);
    allAttacks.sort(
        Comparator.comparing(g -> g.getValue0().getInitiative(), Comparator.reverseOrder()));

    // we need to check if damage is dealt to the defending groups, if group units don't change we
    // end it up in a deadlock
    final var oldImmuneGroupUnits =
        immuneSystem.stream()
            .mapToInt(g -> g.numberOfUnits)
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new));
    final var oldInfectionGroupUnits =
        infectionSystem.stream()
            .mapToInt(g -> g.numberOfUnits)
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new));
    for (var attack : allAttacks) {
      battle(attack);
    }
    // remove any groups that have zero units
    immuneSystem.removeIf(g -> g.numberOfUnits == 0);
    infectionSystem.removeIf(g -> g.numberOfUnits == 0);

    final var newImmuneNumberOfUnits =
        immuneSystem.stream()
            .mapToInt(g -> g.numberOfUnits)
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new));
    final var newInfectionNumberOfUnits =
        infectionSystem.stream()
            .mapToInt(g -> g.numberOfUnits)
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new));
    return oldImmuneGroupUnits.size() != newImmuneNumberOfUnits.size()
        || !oldImmuneGroupUnits.containsAll(newImmuneNumberOfUnits)
        || !newImmuneNumberOfUnits.containsAll(oldImmuneGroupUnits)
        || oldInfectionGroupUnits.size() != newInfectionNumberOfUnits.size()
        || !oldInfectionGroupUnits.containsAll(newInfectionNumberOfUnits)
        || !newInfectionNumberOfUnits.containsAll(oldInfectionGroupUnits);
  }

  private boolean isBattleOver() {
    // check to see if the battle is over and one army is victorious
    return immuneSystem.size() == 0 || infectionSystem.size() == 0;
  }

  private void battle(Pair<Group, Optional<Group>> attack) {
    int damage;

    if (attack.getValue1().isEmpty()) {
      return;
    }
    
    Group attackingGroup = attack.getValue0();
    Group defendingGroup = attack.getValue1().get();

    if (defendingGroup.immunities.contains(attackingGroup.attackType)) {
      damage = 0;
    } else if (defendingGroup.weaknesses.contains(attackingGroup.attackType)) {
      damage = attackingGroup.effectivePower() * 2;
    } else {
      damage = attackingGroup.effectivePower();
    }

    int groupHitPoints = defendingGroup.hitPoint * defendingGroup.numberOfUnits;
    if (damage >= groupHitPoints) {
      defendingGroup.numberOfUnits = 0;
    } else {
      defendingGroup.numberOfUnits -= damage / defendingGroup.hitPoint;
    }
  }

  private List<Pair<Group, Optional<Group>>> getAttackAndDefendGroupPair(
      List<Group> attackGroups, List<Group> defendGroups) {
    List<Pair<Group, Optional<Group>>> attackDefendPairGroups = new ArrayList<>();
    for (var attackingGroup : attackGroups) {
      var defendingGroup = getMostDamageGroup(attackingGroup, defendGroups);
      defendingGroup.ifPresent(defendGroups::remove);
      attackDefendPairGroups.add(new Pair<>(attackingGroup, defendingGroup));
    }

    return attackDefendPairGroups;
  }

  private Optional<Group> getMostDamageGroup(Group group, List<Group> enemyGroups) {
    List<Group> groupsWithMostDamage = new ArrayList<>();
    int damage;
    int maxDamage = Integer.MIN_VALUE;
    for (var g : enemyGroups) {
      if (g.immunities.contains(group.attackType)) {
        continue;
      } else if (g.weaknesses.contains(group.attackType)) {
        damage = group.effectivePower() * 2;
      } else {
        damage = group.effectivePower();
      }

      if (damage > maxDamage) {
        groupsWithMostDamage.clear();
        groupsWithMostDamage.add(g);
        maxDamage = damage;
      } else if (damage == maxDamage) {
        groupsWithMostDamage.add(g);
      }
    }

    // do we have more then one group to attack
    if (groupsWithMostDamage.size() > 1) {
      groupsWithMostDamage.sort(
          Comparator.comparing(Group::effectivePower)
              .thenComparing(Group::getInitiative)
              .reversed());
      return groupsWithMostDamage.stream().findFirst();
    } else if (groupsWithMostDamage.size() == 1) {
      return groupsWithMostDamage.stream().findFirst();
    }

    return Optional.empty();
  }

  private int getRemainingUnitsForSystem(int boost, BattleSystem system) {
    // we need to work with the copies, in order to preserve original after each iteration
    var immuneSystemClone =
        immuneSystem.stream()
            .map(
                g ->
                    new Group(
                        g.groupId,
                        g.numberOfUnits,
                        g.hitPoint,
                        g.attackDamage,
                        g.attackType,
                        g.initiative,
                        g.battleSystem,
                        g.weaknesses,
                        g.immunities))
            .collect(Collectors.toCollection(ArrayList::new));
    var infectionSystemClone =
        infectionSystem.stream()
            .map(
                g ->
                    new Group(
                        g.groupId,
                        g.numberOfUnits,
                        g.hitPoint,
                        g.attackDamage,
                        g.attackType,
                        g.initiative,
                        g.battleSystem,
                        g.weaknesses,
                        g.immunities))
            .collect(Collectors.toCollection(ArrayList::new));
    immuneSystem.forEach(g -> g.setAttackDamage(g.attackDamage + boost));
    do {
      if (!attackPhase(
          targetSelection(immuneSystem, infectionSystem),
          targetSelection(infectionSystem, immuneSystem))) {
        immuneSystem = immuneSystemClone;
        infectionSystem = infectionSystemClone;
        return -1;
      }

    } while (!isBattleOver());
    int remainingUnits = -1;
    if (system == BattleSystem.INFECTION) {
      remainingUnits = infectionSystem.stream().mapToInt(g -> g.numberOfUnits).sum();
    } else {
      if (immuneSystem.size() != 0) {
        remainingUnits = immuneSystem.stream().mapToInt(g -> g.numberOfUnits).sum();
      }
    }
    immuneSystem = immuneSystemClone;
    infectionSystem = infectionSystemClone;
    return remainingUnits;
  }

  private int getBoostValueForImmuneSystemToWin() {
    // use binary search to find the leftmost boost value
    int l = 0;
    // use the upper bound value from the input example and just check all the below values
    int r = 1570;
    while (l < r) {
      int m = Math.floorDiv((l + r), 2);
      if (getRemainingUnitsForSystem(m, BattleSystem.IMMUNE) == -1) {
        l = m + 1;
      } else {
        r = m;
      }
    }
    return l;
  }

  int getResult1() {
    return getRemainingUnitsForSystem(0, BattleSystem.INFECTION);
  }

  int getResult2() {
    int boost = getBoostValueForImmuneSystemToWin();
    return getRemainingUnitsForSystem(boost, BattleSystem.IMMUNE);
  }

  static class Group {
    final int groupId;
    int numberOfUnits;
    final int hitPoint;
    int attackDamage;
    final AttackType attackType;

    Group(
        int groupId,
        int numberOfUnits,
        int hitPoint,
        int attackDamage,
        AttackType attackType,
        int initiative,
        BattleSystem battleSystem,
        List<AttackType> weaknesses,
        List<AttackType> immunities) {
      this.groupId = groupId;
      this.numberOfUnits = numberOfUnits;
      this.hitPoint = hitPoint;
      this.attackDamage = attackDamage;
      this.attackType = attackType;
      this.initiative = initiative;
      this.battleSystem = battleSystem;
      this.weaknesses = weaknesses;
      this.immunities = immunities;
    }

    void setAttackDamage(int attackDamage) {
      this.attackDamage = attackDamage;
    }

    @Override
    public String toString() {
      return "Group{"
          + "groupId="
          + groupId
          + ", numberOfUnits="
          + numberOfUnits
          + ", hitPoint="
          + hitPoint
          + ", attackDamage="
          + attackDamage
          + ", attackType="
          + attackType
          + ", initiative="
          + initiative
          + ", battleSystem="
          + battleSystem
          + ", weaknesses="
          + weaknesses
          + ", immunities="
          + immunities
          + '}';
    }

    int getInitiative() {
      return initiative;
    }

    int effectivePower() {
      return numberOfUnits * attackDamage;
    }

    final int initiative;
    final BattleSystem battleSystem;
    final List<AttackType> weaknesses;
    final List<AttackType> immunities;
  }

  enum BattleSystem {
    IMMUNE,
    INFECTION
  }

  enum AttackType {
    Bludgeoning,
    Cold,
    Fire,
    Radiation,
    Slashing;

    static AttackType from(String attackType) {
      AttackType output;
      switch (attackType) {
        case "bludgeoning":
          output = Bludgeoning;
          break;
        case "cold":
          output = Cold;
          break;
        case "fire":
          output = Fire;
          break;
        case "radiation":
          output = Radiation;
          break;
        case "slashing":
          output = Slashing;
          break;
        default:
          throw new IllegalArgumentException("Invalid attack type!");
      }
      return output;
    }
  }
}
