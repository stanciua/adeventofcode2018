import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day3 {
    private Claim[] claims;
    private int[][] area;

    Day3() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/java/input.txt")));
        claims = input.lines().map(Claim::new).toArray(Claim[]::new);
    }

    int getResult1() {
        // allocate maximum square matrix
        int max_size = Integer.max(Claim.maxHeight + Claim.maxTopEdge,Claim.maxWidth + Claim.maxLeftEdge);
        area = new int[max_size][max_size];
        for (int i = 0; i < area.length; i++) {
            for (int j = 0; j < area[i].length; j++) {
                area[i][j] = 0;
            }
        }
        int overlapNumber = 0;
        for (var claim : claims) {
            // draw each rectangle starting at (leftEdge, topEdge) with (width, height)
            for (int r = claim.topEdge; r < claim.topEdge + claim.height; r++) {
                for (int c = claim.leftEdge; c < claim.leftEdge + claim.width; c++) {
                    if (area[r][c] == 0) {
                        area[r][c] = claim.id;
                    } else if (area[r][c] != -1) {
                        overlapNumber++;
                        area[r][c] = -1;
                    }
                }
            }
        }
        return overlapNumber;
    }

    int getResult2() {
        main_loop:
        for (Claim claim : claims) {
            for (int r = claim.topEdge; r < claim.topEdge + claim.height; r++) {
                for (int c = claim.leftEdge; c < claim.leftEdge + claim.width; c++) {
                    if (area[r][c] == -1) {
                        continue main_loop;
                    }
                }
            }
            return claim.id;
        }

        return -1;
    }
}

final class Claim {
    final int id;
    final int leftEdge;
    final int topEdge;
    final int width;
    final int height;
    static int maxLeftEdge;
    static int maxTopEdge;
    static int maxWidth;
    static int maxHeight;

    Claim(@NotNull String claim) {
        Pattern pattern = Pattern.compile("#(\\d+)\\s*@\\s*(\\d+),(\\d+):\\s*(\\d+)x(\\d+)");
        Matcher match = pa  ttern.matcher(claim);
        if(match.matches()) {
            id = Integer.valueOf(match.group(1));
            leftEdge = Integer.valueOf(match.group(2));
            maxLeftEdge = Integer.max(maxLeftEdge, leftEdge);
            topEdge = Integer.valueOf(match.group(3));
            maxTopEdge = Integer.max(maxTopEdge, topEdge);
            width = Integer.valueOf(match.group(4));
            maxWidth = Integer.max(maxWidth, width);
            height = Integer.valueOf(match.group(5));
            maxHeight = Integer.max(maxHeight, height);
        } else {
            id = 0;
            leftEdge = 0;
            topEdge = 0;
            width = 0;
            height = 0;
        }
    }

}
