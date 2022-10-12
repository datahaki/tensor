// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.tmp.ResamplingMethods;
import ch.alpine.tensor.tmp.TimeSeries;

/** the demo shows that the 2 synchronizations are both necessary and sufficient.
 * removing either of the two "synchronized (timeSeries)" will immediately cause
 * a ConcurrentModificationException to be thrown
 * 
 * on jans pc the demo runs for 5[s] after which the following lines are printed:
 * 
 * element count=3632
 * iterations=3611 */
/* package */ enum SynchronizationDemo {
  ;
  private static final double SEC = 5;

  private static Scalar spawn() {
    return RandomVariate.of(NormalDistribution.standard());
  }

  public static void main(String[] args) throws InterruptedException {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.HOLD_VALUE_FROM_LEFT);
    Timing timing = Timing.started();
    launchThread(timing, timeSeries);
    while (timing.seconds() < SEC) {
      // removing the following line: "synchronized (timeSeries)"
      // ... causes the demo to immediately throw a ConcurrentModificationException
      synchronized (timeSeries) // comment out line in order for demo to crash immediately
      //
      {
        timeSeries.insert(spawn(), spawn());
      }
      Thread.sleep(1);
    }
    System.out.println("element count=" + timeSeries.size());
  }

  public static void launchThread(Timing timing, TimeSeries timeSeries) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        int iterations = 0;
        while (timing.seconds() < SEC) {
          // removing the following line: "synchronized (timeSeries)"
          // ... causes the demo to immediately throw a ConcurrentModificationException
          synchronized (timeSeries) // comment out line in order for demo to crash immediately
          //
          {
            if (!timeSeries.isEmpty()) {
              Scalar sum = RealScalar.ZERO;
              for (Scalar scalar : timeSeries.keySet(timeSeries.domain(), true)) {
                sum = sum.add(scalar);
              }
              ++iterations;
            }
          }
          try {
            Thread.sleep(1);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
        System.out.println("iterations=" + iterations);
      }
    }).start();
  }
}
