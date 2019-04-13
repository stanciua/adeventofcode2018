import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day20 {
  Day20() throws Exception {
    String input = Files.readString(Path.of("src/test/java/input.txt"));
    System.out.println(reduceInput(input));
  }

  String reduceForm(String inputForm) {
    int openParenPos = inputForm.indexOf('(');
    int closeParenPos = inputForm.indexOf(')');
    String firstToken = inputForm.substring(0, openParenPos);
    List<String> tokens =
        Arrays.stream(inputForm.substring(openParenPos + 1, closeParenPos).split("\\|"))
            .collect(Collectors.toCollection(ArrayList::new));
    // check for the empty option case
    if (inputForm.charAt(closeParenPos - 1) == '|') {
      tokens.add(new String()); 
    }
    return tokens.stream().map(t -> firstToken + t).collect(Collectors.joining("|"));
  }

  String reduceInput(String input) {
    // ^ENWWW(NEEE|SSE(EE|N))$
    // Here's an idea:
    //   - go to the most inner term of form: SSE(EE|N)
    //   - then reduce it to SSEEE | SSEN
    //   - repeat the process until we have a large string that separates each path with | as:
    //       ^ENWWWNEEE|ENWWWSSEEE|ENWWWSSEN$
    StringBuilder output = new StringBuilder(input);
    int lastIndexOpenParen = output.lastIndexOf("(");
    while (lastIndexOpenParen != -1) {
      int lastIndexCloseParen = output.indexOf(")", lastIndexOpenParen);
      int startPos = getFirstTokenPosBeforeOpenParen(output, lastIndexOpenParen - 1);
      String reducedForm = reduceForm(output.substring(startPos, lastIndexCloseParen + 1));
      output.replace(startPos, lastIndexCloseParen + 1, reducedForm);
      lastIndexOpenParen = output.lastIndexOf("(");
    }
    return output.toString();
  }

  int getFirstTokenPosBeforeOpenParen(StringBuilder input, int firstPosition) {
    while (firstPosition >= 0) {
      char c = input.charAt(firstPosition);
      if (c != 'N' && c != 'S' && c != 'E' && c != 'W') {
        break;
      }
      firstPosition--;
    }
    return firstPosition + 1;
  }

  int getResult1() {
    return -1;
  }

  int getResult2() {
    return -1;
  }
}
