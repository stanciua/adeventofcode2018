import org.javatuples.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day24 {
  List<Group> infectionSystem;
  List<Group> immuneSystem;
  private static final Pattern ImmuneSystemPattern = Pattern.compile("Immune System:");
  private static final Pattern InfectionPattern = Pattern.compile("Infection:");
  private static final Pattern groupPattern =
      Pattern.compile(
          "(\\d+) units each with (\\d+) hit points (\\(.+\\) )?with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)");

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
        List<AttackType> weaknesses = getWeakness(weaknessAndImmunities);
        List<AttackType> immunities = getImmunities(weaknessAndImmunities);
        int attackDamage = Integer.valueOf(matcher.group(4));
        AttackType attackType = AttackType.from(matcher.group(5));
        int initiative = Integer.valueOf(matcher.group(6));
        BattleSystem battleSystem = BattleSystem.immune;
        if (isInfectionSystem) {
          battleSystem = BattleSystem.infection;
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

  static List<AttackType> getWeakness(String weaknessesAndImmunities) {
    List<AttackType> output = new ArrayList<>();
    if (weaknessesAndImmunities == null || weaknessesAndImmunities.length() == 0) {
      return output;
    }

    if (!weaknessesAndImmunities.contains("weak to ")) {
      return output;
    }

    int indexOfWeaknesses = weaknessesAndImmunities.indexOf("weak to ") + "weak to ".length();
    int indexOfEndWeaknesses = weaknessesAndImmunities.indexOf(";", indexOfWeaknesses);
    if (indexOfEndWeaknesses == -1) {
      indexOfEndWeaknesses = weaknessesAndImmunities.indexOf(")", indexOfWeaknesses);
    }

    String weaknesses = weaknessesAndImmunities.substring(indexOfWeaknesses, indexOfEndWeaknesses);
    Arrays.stream(weaknesses.split(","))
        .filter(e -> e.length() != 0)
        .forEach(e -> output.add(AttackType.from(e.strip())));
    return output;
  }

  static List<AttackType> getImmunities(String weaknessesAndImmunities) {
    List<AttackType> output = new ArrayList<>();
    if (weaknessesAndImmunities == null || weaknessesAndImmunities.length() == 0) {
      return output;
    }

    if (!weaknessesAndImmunities.contains("immune to ")) {
      return output;
    }

    int indexOfWeaknesses = weaknessesAndImmunities.indexOf("immune to ") + "immune to ".length();
    int indexOfEndWeaknesses = weaknessesAndImmunities.indexOf(";", indexOfWeaknesses);
    if (indexOfEndWeaknesses == -1) {
      indexOfEndWeaknesses = weaknessesAndImmunities.indexOf(")", indexOfWeaknesses);
    }

    String weaknesses = weaknessesAndImmunities.substring(indexOfWeaknesses, indexOfEndWeaknesses);
    Arrays.stream(weaknesses.split(","))
        .filter(e -> e.length() != 0)
        .forEach(e -> output.add(AttackType.from(e.strip())));
    return output;
  }

  List<Pair<Group, Optional<Group>>> targetSelection(List<Group> attackers, List<Group> defenders) {
    List<Group> attackersGroupOrder =
        attackers.stream()
            .sorted(
                Comparator.comparing(Group::effectivePower)
                    .thenComparing(Group::getInitiative)
                    .reversed())
            .collect(Collectors.toCollection(ArrayList::new));
    // next we need pair one attacking group to one defending group
    return getAttackAndDefendGroupPair(
        attackersGroupOrder, defenders.stream().collect(Collectors.toCollection(ArrayList::new)));
  }

  boolean attackPhase(
      List<Pair<Group, Optional<Group>>> immune, List<Pair<Group, Optional<Group>>> infection) {
    // we put all the attacks in on place in order to sorted them based on reverse initiative
    var allAttacks = new ArrayList<Pair<Group, Optional<Group>>>();
    allAttacks.addAll(immune);
    allAttacks.addAll(infection);
    allAttacks.sort(
        Comparator.comparing(g -> g.getValue0().getInitiative(), Comparator.reverseOrder()));

    // we need to check if damage is dealt to the defending groups, if group units don't change we
    // end it up in a deadlock
    var oldImmuneGroupUnits =
        immuneSystem.stream()
            .mapToInt(g -> g.numberOfUnits)
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new));
    var oldInfectionGroupUnits =
        infectionSystem.stream()
            .mapToInt(g -> g.numberOfUnits)
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new));
    for (var attack : allAttacks) {
      if (attack.getValue1().isEmpty()) {
        continue;
      }
      if (!battle(attack)) {
        return false;
      }
    }
    // remove any groups that have zero units
    immuneSystem.removeIf(g -> g.numberOfUnits == 0);
    infectionSystem.removeIf(g -> g.numberOfUnits == 0);

    var newImmuneNumberOfUnits =
        immuneSystem.stream()
            .mapToInt(g -> g.numberOfUnits)
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new));
    var newInfectionNumberOfUnits =
        infectionSystem.stream()
            .mapToInt(g -> g.numberOfUnits)
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new));
    if (oldImmuneGroupUnits.size() == newImmuneNumberOfUnits.size()
        && oldImmuneGroupUnits.containsAll(newImmuneNumberOfUnits)
        && newImmuneNumberOfUnits.containsAll(oldImmuneGroupUnits)
        && oldInfectionGroupUnits.size() == newInfectionNumberOfUnits.size()
        && oldInfectionGroupUnits.containsAll(newInfectionNumberOfUnits)
        && newInfectionNumberOfUnits.containsAll(oldInfectionGroupUnits)) {
      return false;
    }

    return true;
  }

  boolean isBattleOver() {
    // check to see if the battle is over and one army is victorious
    if (immuneSystem.size() == 0 || infectionSystem.size() == 0) {
      return true;
    }
    return false;
  }

  int getResult1() {
      return getRemainingUnitsForSystem(0, BattleSystem.infection);
  }

  boolean battle(Pair<Group, Optional<Group>> attack) {
    int damage;

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
    return true;
  }

  List<Pair<Group, Optional<Group>>> getAttackAndDefendGroupPair(
      List<Group> attackGroups, List<Group> defendGroups) {
    List<Pair<Group, Optional<Group>>> attackDefendPairGroups = new ArrayList<>();
    for (var attackingGroup : attackGroups) {
      var defendingGroup = getMostDamageGroup(attackingGroup, defendGroups);
      if (defendingGroup.isPresent()) {
        defendGroups.remove(defendingGroup.get());
      }
      attackDefendPairGroups.add(new Pair<>(attackingGroup, defendingGroup));
    }

    return attackDefendPairGroups;
  }

  Optional<Group> getMostDamageGroup(Group group, List<Group> enemyGroups) {
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

  int getRemainingUnitsForSystem(int boost, BattleSystem system) {
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
    while (true) {
      if (!attackPhase(
          targetSelection(immuneSystem, infectionSystem),
          targetSelection(infectionSystem, immuneSystem))) {
        return -1;
      }

      if (isBattleOver()) {
        break;
      }
    }
    int remainingUnits = -1;
    if (system == BattleSystem.infection) {
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

  int getBoostValueForImmuneSystemToWin() {
    // use binary search to find the leftmost boost value
    int l = 0;
    int r = 1570;
    while (l < r) {
      int m = Math.floorDiv((l + r), 2);
      System.out.println(m);
      if (getRemainingUnitsForSystem(m, BattleSystem.immune) == -1) {
        l = m + 1;
      } else {
        r = m;
      }
    }
    return l;
  }

  int getResult2() {
    int boost = getBoostValueForImmuneSystemToWin();
    return getRemainingUnitsForSystem(boost, BattleSystem.immune);
  }

  static class Group {
    int groupId;
    int numberOfUnits;
    int hitPoint;
    int attackDamage;
    AttackType attackType;

    public Group(
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

    public void setAttackDamage(int attackDamage) {
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

    public int getInitiative() {
      return initiative;
    }

    int effectivePower() {
      return numberOfUnits * attackDamage;
    }

    int initiative;
    BattleSystem battleSystem;
    List<AttackType> weaknesses;
    List<AttackType> immunities;
  }

  enum BattleSystem {
    immune,
    infection
  }

  enum AttackType {
    Bludgeoning,
    Cold,
    Fire,
    Radiation,
    Slashing;

    static AttackType from(String attackType) {
      AttackType output = Bludgeoning;
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
