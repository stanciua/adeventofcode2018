import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day21 {

  private int ipBoundRegister = 0;
  private final List<Instruction> instructions;

  private static final Pattern ipBoundRegisterPattern =
      Pattern.compile("#ip\\s(\\d)");
  private static final Pattern instructionPattern =
      Pattern.compile("(\\w+)\\s(\\d+)\\s(\\d+)\\s(\\d+)");

  Day21() throws Exception {
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

  private void executeFastIntegerDivision(int[] inputRegisters, int[] outputRegisters) {
    outputRegisters[3] = inputRegisters[5] / 256;
    outputRegisters[1] = outputRegisters[3] + 1;
    outputRegisters[ipBoundRegister] = 18;
  }

  int getResult1() {
    int[] inputRegisters = {0, 0, 0, 0, 0, 0};
    int[] outputRegisters = {0, 0, 0, 0, 0, 0};
    int ip = 0;
    while (ip < instructions.size()) {
      inputRegisters[ipBoundRegister] = ip;
      if (ip == 18) {
        executeFastIntegerDivision(inputRegisters, outputRegisters);
      } else if (ip == 28) {
        // the first value in register 2 is the value to be set in register 0 in order for the 
        // program to terminate 
        return outputRegisters[2];
      } else {
        execute(instructions.get(ip).getOpcode(), inputRegisters, outputRegisters,
            instructions.get(ip));
      }
      System.arraycopy(outputRegisters, 0, inputRegisters, 0, outputRegisters.length);
      ip = inputRegisters[ipBoundRegister] + 1;
    }
    return outputRegisters[0];
  }

  int getResult2() {
    int[] inputRegisters = {0, 0, 0, 0, 0, 0};
    int[] outputRegisters = {0, 0, 0, 0, 0, 0};
    int ip = 0;
    List<Integer> seenValues = new ArrayList<>();
    while (ip < instructions.size()) {
      inputRegisters[ipBoundRegister] = ip;
      if (ip == 18) {
        executeFastIntegerDivision(inputRegisters, outputRegisters);
      } else if (ip == 28) {
        outputRegisters[3] = 0;
        outputRegisters[ipBoundRegister] = 28;
        if (seenValues.stream().anyMatch(v -> v == outputRegisters[2])) {
          break;
        } else {
          seenValues.add(outputRegisters[2]);
        }
      } else {
        execute(instructions.get(ip).getOpcode(), inputRegisters, outputRegisters,
            instructions.get(ip));
      }
      System.arraycopy(outputRegisters, 0, inputRegisters, 0, outputRegisters.length);
      ip = inputRegisters[ipBoundRegister] + 1;
    }
    // the value of register 0 will be then one in register 2 after we loop again the same value in
    // register 2
    return seenValues.get(seenValues.size() - 1);
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
    EQRR
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
