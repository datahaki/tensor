// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;

/* package */ enum NormalizeDemo {
  ;
  public static void main(String[] args) {
    Distribution distribution = NormalDistribution.standard();
    Scalar min = RealScalar.ONE;
    Scalar max = RealScalar.ONE;
    for (int c = 0; c < 10000; ++c) {
      Tensor vector = RandomVariate.of(distribution, 1000);
      Tensor result = Vector2Norm.NORMALIZE.apply(vector);
      Scalar value = Vector2Norm.of(result);
      min = Min.of(min, value);
      max = Max.of(max, value);
    }
    System.out.println(min + " " + max);
  }
}
