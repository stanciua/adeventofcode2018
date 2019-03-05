import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  void addr(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    outputRegisters[instruction.outputC] =
        outputRegisters[instruction.inputA] + outputRegisters[instruction.inputB];
  }

  void addi(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    outputRegisters[instruction.outputC] =
        outputRegisters[instruction.inputA] + instruction.inputB;
  }

  void mulr(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    outputRegisters[instruction.outputC] =
        outputRegisters[instruction.inputA] * outputRegisters[instruction.inputB];
  }

  void muli(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    outputRegisters[instruction.outputC] =
        outputRegisters[instruction.inputA] * instruction.inputB;
  }

  void banr(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    outputRegisters[instruction.outputC] =
        outputRegisters[instruction.inputA] & outputRegisters[instruction.inputB];
  }

  void bani(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    outputRegisters[instruction.outputC] =
        outputRegisters[instruction.inputA] & instruction.inputB;
  }


  void borr(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    outputRegisters[instruction.outputC] =
        outputRegisters[instruction.inputA] | outputRegisters[instruction.inputB];
  }

  void bori(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    outputRegisters[instruction.outputC] =
        outputRegisters[instruction.inputA] | instruction.inputB;
  }

  void setr(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    outputRegisters[instruction.outputC] =
        outputRegisters[instruction.inputA];
  }

  void seti(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    outputRegisters[instruction.outputC] =
        instruction.inputA;
  }

  void gtir(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    if (instruction.inputA > outputRegisters[instruction.inputB]) {
      outputRegisters[instruction.outputC] = 1;
    } else {
      outputRegisters[instruction.outputC] = 0;
    }
  }

  void gtri(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    if (outputRegisters[instruction.inputA] > instruction.inputB) {
      outputRegisters[instruction.outputC] = 1;
    } else {
      outputRegisters[instruction.outputC] = 0;
    }
  }

  void gtrr(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    if (outputRegisters[instruction.inputA] > outputRegisters[instruction.inputB]) {
      outputRegisters[instruction.outputC] = 1;
    } else {
      outputRegisters[instruction.outputC] = 0;
    }
  }


  void eqir(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    if (instruction.inputA == outputRegisters[instruction.inputB]) {
      outputRegisters[instruction.outputC] = 1;
    } else {
      outputRegisters[instruction.outputC] = 0;
    }
  }

  void eqri(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    if (outputRegisters[instruction.inputA] == instruction.inputB) {
      outputRegisters[instruction.outputC] = 1;
    } else {
      outputRegisters[instruction.outputC] = 0;
    }
  }

  void eqrr(int[] inputRegisters, int[] outputRegisters, Instruction instruction) {
    System.arraycopy(inputRegisters, 0, outputRegisters, 0, inputRegisters.length);
    if (outputRegisters[instruction.inputA] == outputRegisters[instruction.inputB]) {
      outputRegisters[instruction.outputC] = 1;
    } else {
      outputRegisters[instruction.outputC] = 0;
    }
  }

  void updateCounterIfEqual(int index, int[] outputRegisters, Map<String, Integer> countMap) {
    if (Arrays.equals(outputRegisters, afterRegisters.get(index))) {
      Instruction instruction = instructions.get(index);
      String key = instruction.getInputA() + " " + instruction.getInputB() + " " + instruction.getOutputC();
      countMap.put(key,
          countMap.getOrDefault(key, 0) + 1);
    }
  }

  int getResult1() {
    Map<String, Integer> countMap = new HashMap<>();
    int[] outputRegisters = new int[4];
    for (int i = 0; i < instructions.size(); i++) {
      addr(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      addi(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      mulr(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      muli(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      banr(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      bani(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      borr(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      bori(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      setr(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      seti(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      gtir(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      gtri(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      gtrr(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      eqir(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      eqri(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
      eqrr(beforeRegisters.get(i), outputRegisters, instructions.get(i));
      updateCounterIfEqual(i, outputRegisters, countMap);
    }

//    System.out.println(countMap.entrySet().stream().filter(e -> e.getValue() >= 3).count());
    System.out.println(countMap);
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
          ", inputB=" + inputB +
          ", outputC=" + outputC +
          '}';
    }

    public Opcode getOpcode() {
      return opcode;
    }

    public Instruction(Opcode opcode, int inputA, int inputB, int outputC) {
      this.opcode = opcode;
      this.inputA = inputA;
      this.inputB = inputB;
      this.outputC = outputC;
    }

    public int getInputA() {
      return inputA;
    }

    public void setInputA(int inputA) {
      this.inputA = inputA;
    }

    public int getInputB() {
      return inputB;
    }

    public void setInputB(int inputB) {
      this.inputB = inputB;
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
    int inputB;
    int outputC;
  }
}
