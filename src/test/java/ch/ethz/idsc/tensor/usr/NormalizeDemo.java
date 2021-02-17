// code by jph
package ch.ethz.idsc.tensor.usr;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;

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
