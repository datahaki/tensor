// code by jph
package ch.alpine.tensor.usr;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.ext.BoundedLinkedList;
import ch.alpine.tensor.ext.Timing;

public enum BoundedLinkedListDemo {
  ;
  public static void main(String[] args) {
    Timing timing = Timing.started();
    double timeout = 3;
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(12);
    RandomGenerator randomGenerator = new SecureRandom();
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
