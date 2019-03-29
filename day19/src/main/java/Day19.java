import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day19 {

  int ipBoundRegister = 0;
  List<Instruction> instructions;

  private static final Pattern ipBoundRegisterPattern =
      Pattern.compile("#ip\\s(\\d)");
  private static final Pattern instructionPattern =
      Pattern.compile("(\\w+)\\s(\\d+)\\s(\\d+)\\s(\\d+)");

  Day19() throws Exception {
    instructions = new ArrayList<>();
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    for (String line : lines) {
      Matcher match = ipBoundRegisterPattern.matcher(line);
      if (match.matches()) {
        ipBoundRegister = Integer.valueOf(match.group(1));
        continue;
      }
      match = instructionPattern.matcher(line);
      if (match.matches()) {
        Instruction instruction = new Instruction(
            Opcode.valueOf(match.group(1).toUpperCase()),
            Integer.parseInt(match.group(2)), Integer.parseInt(match.group(3)),
            Integer.parseInt(match.group(4)));
        instructions.add(instruction);
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

  int getResult1() {
    int[] inputRegisters = {0, 0, 0, 0, 0, 0};
    int[] outputRegisters = {0, 0, 0, 0, 0, 0};
    int ip = 0;
    int count = 0;
    while (ip < instructions.size()) {
      inputRegisters[ipBoundRegister] = ip;
      execute(instructions.get(ip).getOpcode(), inputRegisters, outputRegisters,
          instructions.get(ip));
      System.arraycopy(outputRegisters, 0, inputRegisters, 0, outputRegisters.length);
      ip = inputRegisters[ipBoundRegister] + 1;
      count++;
    }
    return outputRegisters[0];
  }

  int getResult2() {
    int[] inputRegisters = {1, 0, 0, 0, 0, 0};
    int[] outputRegisters = {0, 0, 0, 0, 0, 0};
    int ip = 0;
    int count = 0;
    while (ip < instructions.size() ) {
      if (ip == 16) {
        System.out.println("boom");
      Arrays.stream(inputRegisters).forEach(e -> System.out.print(e + " "));
      System.out.println();
      System.out.println(instructions.get(ip));
      Arrays.stream(outputRegisters).forEach(e -> System.out.print(e + " "));
      System.out.println();
      }
//      System.out.println(ip);
      inputRegisters[ipBoundRegister] = ip;
//      Arrays.stream(inputRegisters).forEach(e -> System.out.print(e + " "));
//      System.out.println();
//      System.out.println(instructions.get(ip));
      execute(instructions.get(ip).getOpcode(), inputRegisters, outputRegisters,
          instructions.get(ip));
      System.arraycopy(outputRegisters, 0, inputRegisters, 0, outputRegisters.length);
//      Arrays.stream(outputRegisters).forEach(e -> System.out.print(e + " "));
      ip = inputRegisters[ipBoundRegister] + 1;
//      System.out.println();
//      System.out.println();
      count++;
    }
    return outputRegisters[0];
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
