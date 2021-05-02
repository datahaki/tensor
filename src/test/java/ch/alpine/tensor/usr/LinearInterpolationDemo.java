// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;

/** DEMO IS OBSOLETE SINCE LinearInterpolation
 * INTERNALLY USES UNPROTECT */
/* package */ enum LinearInterpolationDemo {
  ;
  private static final Distribution DISTRIBUTION = UniformDistribution.unit();

  private static long time(Tensor tensor) {
    Interpolation interpolation = LinearInterpolation.of(tensor);
    Timing timing = Timing.started();
    for (int count = 1; count < 20000; ++count) {
      interpolation.get(RandomVariate.of(DISTRIBUTION, 3));
    }
    timing.stop();
    return timing.nanoSeconds();
  }

  public static void main(String[] args) {
    Tensor tensor = RandomVariate.of(DISTRIBUTION, 10, 10, 10);
    {
      System.out.println("unprot " + time(Unprotect.references(tensor)));
      System.out.println("normal " + time(tensor));
      System.out.println("unprot " + time(Unprotect.references(tensor)));
      System.out.println("normal " + time(tensor));
      System.out.println("unprot " + time(Unprotect.references(tensor)));
      System.out.println("normal " + time(tensor));
      System.out.println("unprot " + time(Unprotect.references(tensor)));
      System.out.println("normal " + time(tensor));
    }
  }
}
