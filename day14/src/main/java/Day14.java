import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Day14 {
  private final List<Integer> recipes;
  private Integer currentRecipe1;
  private Integer currentRecipe2;

  Day14() {
    recipes = new ArrayList<>(List.of(3, 7));
    currentRecipe1 = 0;
    currentRecipe2 = 1;
  }

  private void createRecipe() {
    int sum = recipes.get(currentRecipe1) + recipes.get(currentRecipe2);
    if (sum >= 10) {
      recipes.add(sum / 10);
      recipes.add(sum % 10);
    } else {
      recipes.add(sum);
    }
  }

  private void updateCurrentRecipe() {
    currentRecipe1 = (currentRecipe1 + recipes.get(currentRecipe1) + 1) % recipes.size();
    currentRecipe2 = (currentRecipe2 + recipes.get(currentRecipe2) + 1) % recipes.size();
  }

  private String getScoreAfterNoOfRecipes() {
    updateCurrentRecipe();
    while (recipes.size() < 306281 + 10) {
      createRecipe();
      updateCurrentRecipe();
    }
    return recipes.stream()
        .skip(306281)
        .limit(10)
        .map(Object::toString)
        .collect(Collectors.joining());
  }

  String getResult1() {
    return getScoreAfterNoOfRecipes();
  }

  private boolean foundInputScore(int[] input, int[] position) {
    if (recipes.size() < input.length + 1) {
      return false;
    }
    int lastIndex = recipes.size() - 1;
    return matchRecipeWithInput(lastIndex, input, position)
        || matchRecipeWithInput(lastIndex - 1, input, position);
  }

  private boolean matchRecipeWithInput(int startPosition, int[] input, int[] position) {
    boolean match = true;
    for (int i = 0; i < input.length; i++) {
      match = match && recipes.get(startPosition - i) == input[input.length - i - 1];
    }

    if (match) {
      position[0] = startPosition - input.length + 1;
    }
    return match;
  }

  long getResult2() {
    updateCurrentRecipe();
    String inputString = "306281";
    int[] input = inputString.chars().map(Character::getNumericValue).toArray();
    int[] position = new int[1];
    while (!foundInputScore(input, position)) {
      createRecipe();
      updateCurrentRecipe();
    }
    return position[0];
  }
}
