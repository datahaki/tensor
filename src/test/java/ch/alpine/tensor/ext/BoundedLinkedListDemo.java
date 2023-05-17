// code by jph
package ch.alpine.tensor.ext;

import java.util.Random;
import java.util.random.RandomGenerator;

public enum BoundedLinkedListDemo {
  ;
  public static void main(String[] args) {
    Timing timing = Timing.started();
    double timeout = 3;
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(12);
    RandomGenerator randomGenerator = new Random();
    new Thread(() -> {
      System.out.println("runA1");
      while (timing.seconds() < timeout)
        synchronized (boundedLinkedList) {
          boundedLinkedList.add(randomGenerator.nextInt());
        }
    }).start();
    new Thread(() -> {
      System.out.println("runA2");
      while (timing.seconds() < timeout) {
        synchronized (boundedLinkedList) {
          boundedLinkedList.add(randomGenerator.nextInt());
        }
      }
    }).start();
    new Thread(() -> {
      System.out.println("runR");
      while (timing.seconds() < timeout) {
        if (!boundedLinkedList.isEmpty()) {
          int poll;
          synchronized (boundedLinkedList) {
            poll = boundedLinkedList.poll();
          }
          System.out.println(poll);
        }
      }
    }).start();
  }
}
