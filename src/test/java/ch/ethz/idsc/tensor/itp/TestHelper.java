// code by jph
package ch.ethz.idsc.tensor.itp;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.Assert;

/* package */ enum TestHelper {
  ;
  static void checkMatch(Interpolation interpolation) {
    Distribution distribution = UniformDistribution.of(0, 2);
    for (int count = 0; count < 20; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      Assert.assertEquals( //
          interpolation.get(Tensors.of(scalar)), //
          interpolation.at(scalar));
    }
  }

  static void checkMatchExact(Interpolation interpolation) {
    Distribution distribution = DiscreteUniformDistribution.of(0, 3);
    for (int count = 0; count < 20; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      Assert.assertEquals( //
          interpolation.get(Tensors.of(scalar)), //
          interpolation.at(scalar));
    }
  }

  static void getScalarFail(Interpolation interpolation) {
    AssertFail.of(() -> interpolation.get(RealScalar.of(1.4)));
    AssertFail.of(() -> interpolation.get(RealScalar.ONE));
  }
}