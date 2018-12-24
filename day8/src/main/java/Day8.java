import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

class Day8 {
  private int[] tree;

  Day8() throws Exception {
    tree =
        Files.lines(Path.of("/Users/stanciua/prog/adeventofcode2018/day8/src/test/java/input.txt"))
            .flatMap(line -> Arrays.stream(line.split(" ")))
            .mapToInt(Integer::parseInt)
            .toArray();
  }

  private Node buildTree(int[] tree) {
    ArrayList<Node> parents = new ArrayList<>();
    int i = 2;
    Node root = new Node(tree[0], tree[1]);
    parents.add(root);
    while (i != tree.length) {
      if (tree[i] == 0) {
        Node child = new Node(tree[i], tree[i + 1]);
        Node parent = parents.get(parents.size() - 1);
        child.setParent(parent);
        parent.addChild(child);
        child.setMetadata(Arrays.copyOfRange(tree, i + 2, i + tree[i + 1] + 2));
        i += tree[i + 1];
        i += 2;
      } else {
        Node parent = parents.get(parents.size() - 1);
        if (parent.noOfChilds == parent.childs.size()) {
          parent.setMetadata(Arrays.copyOfRange(tree, i, i + parent.noOfMetada));
          i += parent.noOfMetada;
          parents.remove(parent);
        } else {
          Node child = new Node(tree[i], tree[i + 1]);
          child.setParent(parent);
          parent.addChild(child);
          parents.add(child);
          i += 2;
        }
      }
    }

    return root;
  }

  private void sumMetadata(Node node, ArrayList<ArrayList<Integer>> metadataTotal) {
    if (node.noOfChilds == 0) {
      return;
    }

    for (Node child : node.childs) {
      metadataTotal.add(child.metadata);
      sumMetadata(child, metadataTotal);
    }
  }

  private int findRootTotalValue(int value, Node node) {
    if (node.noOfChilds != 0) {
      for (int i = 0; i < node.metadata.size(); i++) {
        int m = node.metadata.get(i) - 1;
        if (m < node.childs.size()) {
          value = findRootTotalValue(value, node.childs.get(m));
        }
      }
    } else {
      value += node.metadata.stream().mapToInt(i -> i).sum();
    }

    return value;
  }

  int getResult1() {
    Node root = buildTree(this.tree);
    ArrayList<ArrayList<Integer>> metadataTotal = new ArrayList<>();
    metadataTotal.add(root.metadata);
    sumMetadata(root, metadataTotal);

    return metadataTotal.stream().flatMap(ArrayList::stream).mapToInt(i -> i).sum();
  }

  int getResult2() {
    Node root = buildTree(this.tree);
    return findRootTotalValue(0, root);
  }

  static class Node {
    int noOfChilds;
    int noOfMetada;
    ArrayList<Node> childs;
    ArrayList<Integer> metadata;
    Node parent;

    Node(int noOfChilds, int noOfMetada) {
      this.noOfChilds = noOfChilds;
      this.noOfMetada = noOfMetada;
      this.childs = new ArrayList<>();
      this.metadata = new ArrayList<>();
      this.parent = null;
    }

    void setParent(Node parent) {
      this.parent = parent;
    }

    void addChild(Node child) {
      child.setParent(this);
      childs.add(child);
    }

    void setMetadata(int[] metadata) {
      this.metadata.addAll(
          Arrays.stream(metadata).boxed().collect(Collectors.toCollection(ArrayList::new)));
    }
  }
}
