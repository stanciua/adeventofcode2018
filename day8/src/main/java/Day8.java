import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day8 {
  int[] tree;

  Day8() throws Exception {
    tree =
        Files.lines(Path.of("src/test/java/input.txt"))
            .flatMap(line -> Arrays.stream(line.split(" ")))
            .mapToInt(s -> Integer.parseInt(s))
            .toArray();
  }

  Node buildTree(int[] tree) {
    Node root = new Node(tree[0], tree[1]);
    buildTreeImpl(
        root,
        IntStream.range(2, tree.length)
            .map(i -> tree[i])
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new)));
    return root;
  }

  void buildTreeImpl(Node parent, ArrayList<Integer> tree) {
    if (tree.size() == 0) {
      return;
    }
    if (tree.get(0) == 0) {
      Node child = new Node(tree.get(0), tree.get(1));
      child.setMetadata(IntStream.range(2, 2 + tree.get(1)).map(i -> tree.get(i)).toArray());
      IntStream.range(0, 2 + tree.get(1)).forEach(i -> tree.remove(0));
      parent.addChild(child);
      child.setParent(parent);
      if (parent.noOfChilds != parent.childs.size()) {
        buildTreeImpl(parent, tree);
      } else {
        parent.setMetadata(IntStream.range(0, parent.noOfMetada).map(i -> tree.get(i)).toArray());
        IntStream.range(0, parent.noOfMetada).forEach(i -> tree.remove(0));
      }
    } else {
      if (parent.noOfChilds != parent.childs.size()) {
        Node child = new Node(tree.get(0), tree.get(1));
        parent.addChild(child);
        child.setParent(parent);
        tree.remove(0);
        tree.remove(0);
        if (child.noOfChilds != child.childs.size()) {
          buildTreeImpl(child, tree);
        }
      }
      parent.setMetadata(IntStream.range(0, parent.noOfMetada).map(i -> tree.get(i)).toArray());
      IntStream.range(0, parent.noOfMetada).forEach(i -> tree.remove(0));
      System.out.println(tree.size());
      buildTreeImpl(parent, tree);
    }
  }

  void sumMetadata(Node node, ArrayList<ArrayList<Integer>> metadataTotal) {
    if (node.noOfChilds == 0) {
      return;
    }

    for (Node child : node.childs) {
      System.out.println(child.metadata);
      metadataTotal.add(child.metadata);
      sumMetadata(child, metadataTotal);
    }
  }

  int getResult1() {
    Node root = buildTree(this.tree);
    ArrayList<ArrayList<Integer>> metadataTotal = new ArrayList<>();
    metadataTotal.add(root.metadata);
    System.out.println(root.metadata);
    sumMetadata(root, metadataTotal);

    return metadataTotal.stream().flatMap(meta -> meta.stream()).mapToInt(i -> i.intValue()).sum();
  }

  int getResult2() {
    return -1;
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
