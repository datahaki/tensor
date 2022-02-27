// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/** demonstration that the function call
 * {@link Interpolation#at(Scalar)} is 2-3 times faster than
 * {@link Interpolation#get(Tensor)} */
/* package */ enum FastInterpolationDemo {
  ;
  public static void main(String[] args) {
    Tensor tensor = RandomVariate.of(UniformDistribution.unit(), 30, 3);
    LinearInterpolation linearInterpolation = //
        (LinearInterpolation) LinearInterpolation.of(tensor);
    {
      Tensor rep1 = linearInterpolation.at(RealScalar.of(3));
      System.out.println(rep1);
      rep1.set(RealScalar.ONE::add, 1);
      Tensor rep2 = linearInterpolation.at(RealScalar.of(3));
      System.out.println(rep2);
      // System.exit(0);
    }
    System.out.println("=== DOUBLE");
    for (int count = 0; count < 10; ++count) {
      {
        Scalar a = DoubleScalar.of(4.123);
        Timing timing = Timing.started();
        for (int index = 0; index < 50000; ++index)
          linearInterpolation.at(a);
        System.out.println("at  " + timing.nanoSeconds());
      }
      {
        Tensor b = Tensors.vector(4.123);
        Timing timing = Timing.started();
        for (int index = 0; index < 50000; ++index)
          linearInterpolation.get(b);
        System.out.println("get " + timing.nanoSeconds());
      }
      {
        Scalar a = DoubleScalar.of(4.123);
        Tensor b = Tensors.vector(4.123);
        for (int index = 0; index < 50000; ++index) {
          Tensor r1 = linearInterpolation.at(a);
          Tensor r2 = linearInterpolation.get(b);
          if (!r1.equals(r2))
            System.err.println("wrong");
        }
      }
    }
    System.out.println("=== EXACT");
    for (int count = 0; count < 10; ++count) {
      {
        Scalar a = RationalScalar.of(20, 7);
        Timing timing = Timing.started();
        for (int index = 0; index < 50000; ++index)
          linearInterpolation.at(a);
        System.out.println("at  " + timing.nanoSeconds());
      }
      {
        Tensor b = Tensors.vector(4.123);
        Timing timing = Timing.started();
        for (int index = 0; index < 50000; ++index)
          linearInterpolation.get(b);
        System.out.println("get " + timing.nanoSeconds());
      }
    }
  }
}
