import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day16 {

  int[] registers;
  List<int[]> beforeRegisters;
  List<int[]> afterRegisters;
  List<Instruction> instructions;
  List<Instruction> part2Instructions;

  private static final Pattern beforePattern =
      Pattern.compile("Before:\\s\\[(\\d+),\\s(\\d+),\\s(\\d+),\\s(\\d+)\\]");
  private static final Pattern instructionPattern =
      Pattern.compile("(\\d+)\\s(\\d+)\\s(\\d+)\\s(\\d+)");
  private static final Pattern afterPattern =
      Pattern.compile("After:\\s\\s\\[(\\d+),\\s(\\d+),\\s(\\d+),\\s(\\d+)\\]");
  
  Day16() throws Exception {
    beforeRegisters = new ArrayList<>();
    afterRegisters = new  ArrayList<>();
    instructions = new  ArrayList<>();
    part2Instructions = new  ArrayList<>();
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);
    boolean isEndOfFirstPart = false;
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      
      if (i < lines.length - 2 && line.isEmpty() && lines[i + 1].isEmpty() && lines[i+2].isEmpty()) {
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
              Integer.parseInt(match.group(2)), Integer.parseInt(match.group(2)),
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
          continue;
        }
      } else {
        Matcher match = instructionPattern.matcher(line);
        if (match.matches()) {
          Instruction instruction = new Instruction(
              Opcode.fromOrdinal(Integer.parseInt(match.group(1))),
              Integer.parseInt(match.group(2)), Integer.parseInt(match.group(2)),
              Integer.parseInt(match.group(4)));
          part2Instructions.add(instruction);
        }
      }
    }
  }

  void addr(int[]inputRegisters, int[]outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters,0, inputRegisters.length);
  }
  
  int getResult1() {
    return -1;
  }

  int getResult2() {
    return -1;
  }

  static enum Opcode {
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
    private static Opcode[] allVAlues = values();

    public static Opcode fromOrdinal(int n) {
      return allVAlues[n];
    }
  }

  static class Instruction {

    @Override
    public String toString() {
      return "Instruction{" +
          "opcode=" + opcode +
          ", inputA=" + inputA +
          ", intputB=" + intputB +
          ", outputC=" + outputC +
          '}';
    }

    public Opcode getOpcode() {
      return opcode;
    }

    public Instruction(Opcode opcode, int inputA, int intputB, int outputC) {
      this.opcode = opcode;
      this.inputA = inputA;
      this.intputB = intputB;
      this.outputC = outputC;
    }

    public int getInputA() {
      return inputA;
    }

    public void setInputA(int inputA) {
      this.inputA = inputA;
    }

    public int getIntputB() {
      return intputB;
    }

    public void setIntputB(int intputB) {
      this.intputB = intputB;
    }

    public int getOutputC() {
      return outputC;
    }

    public void setOutputC(int outputC) {
      this.outputC = outputC;
    }

    public void setOpcode(Opcode opcode) {
      this.opcode = opcode;
    }

    Opcode opcode;
    int inputA;
    int intputB;
    int outputC;
  }
}
