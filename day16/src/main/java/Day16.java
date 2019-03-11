import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Day16 {
  private final List<int[]> beforeRegisters;
  private final List<int[]> afterRegisters;
  private final List<Instruction> instructions;
  private final List<Instruction> part2Instructions;

  private static final Pattern beforePattern =
      Pattern.compile("Before:\\s\\[(\\d+),\\s(\\d+),\\s(\\d+),\\s(\\d+)]");
  private static final Pattern instructionPattern =
      Pattern.compile("(\\d+)\\s(\\d+)\\s(\\d+)\\s(\\d+)");
  private static final Pattern afterPattern =
      Pattern.compile("After:\\s\\s\\[(\\d+),\\s(\\d+),\\s(\\d+),\\s(\\d+)]");

  Day16() throws Exception {
    beforeRegisters = new ArrayList<>();
    afterRegisters = new ArrayList<>();
    instructions = new ArrayList<>();
    part2Instructions = new ArrayList<>();
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    boolean isEndOfFirstPart = false;
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];

      if (i < lines.length - 2 && line.isEmpty() && lines[i + 1].isEmpty() && lines[i + 2]
          .isEmpty()) {
        isEndOfFirstPart = true;
      }
      if (!isEndOfFirstPart) {
        Matcher match = beforePattern.matcher(line);
        if (match.matches()) {
          int[] registers = new int[4];
          registers[0] = Integer.valueOf(match.group(1));
          registers[1] = Integer.valueOf(match.group(2));
          registers[2] = Integer.valueOf(match.group(3));
          registers[3] = Integer.valueOf(match.group(4));
          beforeRegisters.add(registers);
          continue;
        }
        match = instructionPattern.matcher(line);
        if (match.matches()) {
          Instruction instruction = new Instruction(
              Opcode.fromOrdinal(Integer.parseInt(match.group(1))),
              Integer.parseInt(match.group(2)), Integer.parseInt(match.group(3)),
              Integer.parseInt(match.group(4)));
          instructions.add(instruction);
          continue;
        }
        match = afterPattern.matcher(line);
        if (match.matches()) {
          int[] registers = new int[4];
          registers[0] = Integer.valueOf(match.group(1));
          registers[1] = Integer.valueOf(match.group(2));
          registers[2] = Integer.valueOf(match.group(3));
          registers[3] = Integer.valueOf(match.group(4));
          afterRegisters.add(registers);
        }
      } else {
        Matcher match = instructionPattern.matcher(line);
        if (match.matches()) {
          Instruction instruction = new Instruction(
              Opcode.fromOrdinal(Integer.parseInt(match.group(1))),
              Integer.parseInt(match.group(2)), Integer.parseInt(match.group(3)),
              Integer.parseInt(match.group(4)));
          part2Instructions.add(instruction);
        }
      }
    }
  }

  private void execute(Opcode opcode, int[] inputRegisters, int[] outputRegisters,
      Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    switch (opcode) {
      case ADDR:
        outputRegisters[instruction.outputC] =
            outputRegisters[instruction.inputA] + outputRegisters[instruction.inputB];
        break;
      case ADDI:
        outputRegisters[instruction.outputC] =
            outputRegisters[instruction.inputA] + instruction.inputB;
        break;
      case MULR:
        outputRegisters[instruction.outputC] =
            outputRegisters[instruction.inputA] * outputRegisters[instruction.inputB];
        break;
      case MULI:
        outputRegisters[instruction.outputC] =
            outputRegisters[instruction.inputA] * instruction.inputB;
        break;
      case BANR:
        outputRegisters[instruction.outputC] =
            outputRegisters[instruction.inputA] & outputRegisters[instruction.inputB];
        break;
      case BANI:
        outputRegisters[instruction.outputC] =
            outputRegisters[instruction.inputA] & instruction.inputB;
        break;
      case BORR:
        outputRegisters[instruction.outputC] =
            outputRegisters[instruction.inputA] | outputRegisters[instruction.inputB];
        break;
      case BORI:
        outputRegisters[instruction.outputC] =
            outputRegisters[instruction.inputA] | instruction.inputB;
        break;
      case SETR:
        outputRegisters[instruction.outputC] =
            outputRegisters[instruction.inputA];
        break;
      case SETI:
        outputRegisters[instruction.outputC] =
            instruction.inputA;
        break;
      case GTIR:
        if (instruction.inputA > outputRegisters[instruction.inputB]) {
          outputRegisters[instruction.outputC] = 1;
        } else {
          outputRegisters[instruction.outputC] = 0;
        }
        break;
      case GTRI:
        if (outputRegisters[instruction.inputA] > instruction.inputB) {
          outputRegisters[instruction.outputC] = 1;
        } else {
          outputRegisters[instruction.outputC] = 0;
        }
        break;
      case GTRR:
        if (outputRegisters[instruction.inputA] > outputRegisters[instruction.inputB]) {
          outputRegisters[instruction.outputC] = 1;
        } else {
          outputRegisters[instruction.outputC] = 0;
        }
        break;
      case EQIR:
        if (instruction.inputA == outputRegisters[instruction.inputB]) {
          outputRegisters[instruction.outputC] = 1;
        } else {
          outputRegisters[instruction.outputC] = 0;
        }
        break;
      case EQRI:
        if (outputRegisters[instruction.inputA] == instruction.inputB) {
          outputRegisters[instruction.outputC] = 1;
        } else {
          outputRegisters[instruction.outputC] = 0;
        }
        break;
      case EQRR:
        if (outputRegisters[instruction.inputA] == outputRegisters[instruction.inputB]) {
          outputRegisters[instruction.outputC] = 1;
        } else {
          outputRegisters[instruction.outputC] = 0;
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid operation received");
    }
  }

  private int getNoOfSamplesData(Map<Opcode, Set<Integer>> opcodeMap) {
    int[] outputRegisters = new int[4];
    Opcode[] opcodes = {Opcode.ADDR,
        Opcode.ADDI,
        Opcode.MULR,
        Opcode.MULI,
        Opcode.BANR,
        Opcode.BANI,
        Opcode.BORR,
        Opcode.BORI,
        Opcode.SETR,
        Opcode.SETI,
        Opcode.GTIR,
        Opcode.GTRI,
        Opcode.GTRR,
        Opcode.EQIR,
        Opcode.EQRI,
        Opcode.EQRR};
    int noOfSamples = 0;
    for (int i = 0; i < instructions.size(); i++) {
      int count = 0;
      for (Opcode opcode : opcodes) {
        execute(opcode, beforeRegisters.get(i), outputRegisters, instructions.get(i));
        if (Arrays.equals(outputRegisters, afterRegisters.get(i))) {
          int value = instructions.get(i).getOpcode().ordinal();
          Set<Integer> set = opcodeMap.getOrDefault(opcode, new HashSet<>());
          set.add(value);
          opcodeMap.put(opcode, set);
          count++;
        }
      }
      if (count >= 3) {
        noOfSamples++;
      }
    }
    return noOfSamples;
  }

  int getResult1() {
    Map<Opcode, Set<Integer>> opcodeMap = new HashMap<>();
    return getNoOfSamplesData(opcodeMap);
  }

  private Map<Opcode, Integer> reduceOpcodeToInteger(
      List<Map.Entry<Opcode, List<Integer>>> list) {
    Map<Opcode, Integer> output = new HashMap<>();
    while (output.size() != list.size()) {
      for (var entry : list) {
        if (entry.getValue().size() == 1) {
          var value = entry.getValue().stream().findFirst().get();
          output.put(entry.getKey(), value);
          for (var removeEntry : list) {
            if (removeEntry.getValue().isEmpty()) {
              continue;
            }
            removeEntry.getValue().remove(value);
          }
        }
      }
    }

    return output;
  }

  int getResult2() {
    Map<Opcode, Set<Integer>> opcodeMap = new HashMap<>();
    getNoOfSamplesData(opcodeMap);

    Map<Opcode, List<Integer>> entryMap = new HashMap<>();
    for (var entry : opcodeMap.entrySet()) {
      entryMap.put(entry.getKey(),
          entry.getValue().stream().sorted().collect(Collectors.toCollection(ArrayList::new)));
    }
    var list = entryMap.entrySet().stream()
        .sorted(Comparator.comparingInt(e -> e.getValue().size()))
        .collect(
            Collectors.toCollection(ArrayList::new));

    var output = reduceOpcodeToInteger(list);
    var opcodeIntegerMap = output.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    int[] inputRegisters = {0, 0, 0, 0};
    int[] outputRegisters = {0, 0, 0, 0};
    for (var instruction : part2Instructions) {
      execute(opcodeIntegerMap.get(instruction.getOpcode().ordinal()), inputRegisters,
          outputRegisters, instruction);
      System.arraycopy(outputRegisters, 0, inputRegisters, 0, outputRegisters.length);
    }

    return inputRegisters[0];
  }

  enum Opcode {
    ADDR,
    ADDI,
    MULR,
    MULI,
    BANR,
    BANI,
    BORR,
    BORI,
    SETR,
    SETI,
    GTIR,
    GTRI,
    GTRR,
    EQIR,
    EQRI,
    EQRR;
    private static final Opcode[] allVAlues = values();

    static Opcode fromOrdinal(int n) {
      return allVAlues[n];
    }
  }

  static class Instruction {

    @Override
    public String toString() {
      return "Instruction{"
          + "opcode=" + opcode
          + ", inputA=" + inputA
          + ", inputB=" + inputB
          + ", outputC=" + outputC
          + '}';
    }

    Opcode getOpcode() {
      return opcode;
    }

    Instruction(Opcode opcode, int inputA, int inputB, int outputC) {
      this.opcode = opcode;
      this.inputA = inputA;
      this.inputB = inputB;
      this.outputC = outputC;
    }

    final Opcode opcode;
    final int inputA;
    final int inputB;
    final int outputC;
  }
}
