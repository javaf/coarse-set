import java.util.*;

class Main {
  static Set<Integer> set;
  static int NUM = 10, OPS = 100;

  // Each thread removes a number from set
  // and adds another number.
  static Thread thread(int rem, int add) {
    Thread t = new Thread(() -> {
      try {
      for(int i=0; i<OPS; i++) {
        boolean had = set.remove(rem);
        if (had) set.add(add);
        Thread.sleep(10);
      }
      log(rem+" -> "+add+" : done");
      }
      catch(InterruptedException e) {}
    });
    t.start();
    return t;
  }

  static void testThreads() {
    log("Positive threads convert -n -> +n.");
    log("Negative threads convert +n -> -n.");
    log("Starting "+NUM+" positive threads ...");
    log("Starting "+NUM+" negative threads ...");
    Thread[] p = new Thread[NUM];
    Thread[] n = new Thread[NUM];
    for (int i=0; i<NUM; i++) {
      p[i] = thread(-i, i);
      n[i] = thread(i, -i);
    }
    try {
    for (int i=0; i<NUM; i++) {
      p[i].join();
      n[i].join();
    }
    }
    catch(InterruptedException e) {}
  }

  static void populateSet() {
    set = new CoarseSet<>();
    for(int i=0; i<NUM; i++)
      set.add(i);
  }

  static boolean validateSet() {
    return set.size() == NUM;
  }

  public static void main(String[] args) {
    populateSet();
    log("Set: "+set.size()+" : "+set);
    testThreads();
    log("Set: "+set.size()+" : "+set);
    log("Result passed? "+validateSet());
  }

  static void log(String x) {
    System.out.println(x);
  }
}
