import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jgrapht.alg.connectivity.BlockCutpointGraph;

class Day7 {

  private static Map<Character, Set<Character>> steps = new HashMap<>();
  private static Map<Character, Set<Character>> ancestors = new HashMap<>();
  private static Set<Character> seenSteps = new TreeSet<>();
  private static final Pattern pattern = Pattern
      .compile("Step ([A-Z]) must be finished before step ([A-Z]) can begin.");
  private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  Day7() throws Exception {
    String[] lines = Files.lines(Path.of("src/test/java/input.txt")).toArray(String[]::new);

    for (String line : lines) {
      Matcher match = pattern.matcher(line);
      if (match.matches()) {
        char parentStep = match.group(1).charAt(0);
        char childStep = match.group(2).charAt(0);

        if (steps.containsKey(parentStep)) {
          steps.get(parentStep).add(childStep);
        } else {
          steps
              .put(parentStep, Stream.of(childStep).collect(Collectors.toCollection(TreeSet::new)));
        }
      }
    }
    for (char c : steps.values().stream().flatMap(Set::stream)
        .collect(Collectors.toCollection(TreeSet::new))) {
      ancestors.put(c,
          steps.entrySet().stream().filter(e -> e.getValue().contains(c)).map(Map.Entry::getKey)
              .collect(Collectors.toCollection(TreeSet::new)));
    }
  }

  private static Set<Character> getStartingSteps() {
    Set<Character> parents = new TreeSet<>(steps.keySet());
    parents.removeAll(steps.values().stream().flatMap(Set::stream)
        .collect(Collectors.toCollection(TreeSet::new)));
    return new TreeSet<>(parents);
  }

  String getResult1() {
    Set<Character> startSteps = getStartingSteps();
    StringBuilder output = new StringBuilder();
    processSteps(output, startSteps);
    return output.toString();
  }

  private void processSteps(StringBuilder output, Set<Character> toBeProcessedSteps) {
    if (toBeProcessedSteps.isEmpty()) {
      return;
    }

    for (char c : toBeProcessedSteps) {
      Set<Character> ancestorForStep = ancestors.get(c);
      if (ancestorForStep != null && !seenSteps.containsAll(ancestorForStep)) {
        continue;
      }
      seenSteps.add(c);
      output.append(c);
      Set<Character> children = steps.get(c);
      if (children != null) {
        toBeProcessedSteps.addAll(children);
      }
      Set<Character> copy = new TreeSet<>(toBeProcessedSteps);
      copy.remove(c);
      processSteps(output, copy);
      return;
    }
  }

  int getResult2() {
    final int BOUND = 26;
    final int PRODUCERS = 1;
    final int CONSUMERS = 2;
    final int POISON_PILL = Integer.MAX_VALUE;
    final int POISON_PILL_PER_PRODUCER = CONSUMERS / PRODUCERS;

    BlockingQueue<Integer> queue = new LinkedBlockingDeque<>(BOUND);
    Thread producer = new Thread(new StepsProducer(queue, POISON_PILL, POISON_PILL_PER_PRODUCER));
    producer.start();
    try {
      producer.join();
    } catch(InterruptedException e){
      e.printStackTrace();
    }

    // start the worker threads
    Thread[] workers = new Thread[CONSUMERS];
    for (int i = 0; i < workers.length; i++) {
      workers[i] = new Thread(new Worker(queue, POISON_PILL));
      workers[i].start();
    }

    try {
      for (int i = 0; i < workers.length; i++) {
        workers[i].join();
      }
    } catch( InterruptedException e) {
      e.printStackTrace();
    }
    return -1;
  }

  static class Worker implements Runnable {
    private BlockingQueue<Integer> queue;
    private final int poisonPill;
    public Worker(BlockingQueue<Integer> queue, int poisonPill) {
      this.queue = queue;
      this.poisonPill = poisonPill;
    }

    public void run() {
      try {
        while(true) {
          int step = queue.take();
          if (step == poisonPill) {
            return;
          }
          for (int i = 0; i < step; i++){
            System.out.println(Thread.currentThread().getName() + " sub-step " + i + " done");
          }
          System.out.println(Thread.currentThread().getName() + " step: " + step);
        }
      }catch(InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }
  static class StepsProducer implements Runnable {
    private BlockingQueue<Integer> stepsQueue;
    private final int poisonPill;
    private final int poisonPillPerProducer;

    public StepsProducer(BlockingQueue<Integer> numbersQueue, int poisonPill, int poisonPillPerProducer) {
      this.stepsQueue = numbersQueue;
      this.poisonPill = poisonPill;
      this.poisonPillPerProducer = poisonPillPerProducer;
    }
    public void run() {
      try {
        Set<Character> startSteps = getStartingSteps();
        produceSteps(startSteps);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    private void produceSteps(Set<Character> toBeProcessedSteps) throws InterruptedException {
      if (toBeProcessedSteps.isEmpty()) {
        return;
      }

      for (char c : toBeProcessedSteps) {
        Set<Character> ancestorForStep = ancestors.get(c);
        if (ancestorForStep != null && !seenSteps.containsAll(ancestorForStep)) {
          continue;
        }
        seenSteps.add(c);
        System.out.println("Produced: " + (ALPHABET.indexOf(c) + 1));
        stepsQueue.put(ALPHABET.indexOf(c) + 1);
        Set<Character> children = steps.get(c);
        if (children != null) {
          toBeProcessedSteps.addAll(children);
        }
        Set<Character> copy = new TreeSet<>(toBeProcessedSteps);
        copy.remove(c);
        produceSteps(copy);

        // fill the poison pill for each consumer to terminate
        for (int i = 0; i<poisonPillPerProducer; i++) {
          stepsQueue.put(poisonPill);
        }
        return;
      }
    }
  }
}

