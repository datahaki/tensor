// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.usr.AssertFail;

/* package */ enum TestHelper {
  ;
  public static void checkMatch(Interpolation interpolation) {
    Distribution distribution = UniformDistribution.of(0, 2);
    for (int count = 0; count < 10; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      assertEquals( //
          interpolation.get(Tensors.of(scalar)), //
          interpolation.at(scalar));
    }
  }

  public static void checkMatchExact(Interpolation interpolation) {
    Distribution distribution = DiscreteUniformDistribution.of(0, 3);
    for (int count = 0; count < 10; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      assertEquals( //
          interpolation.get(Tensors.of(scalar)), //
          interpolation.at(scalar));
    }
  }

  public static void getScalarFail(Interpolation interpolation) {
    AssertFail.of(() -> interpolation.get(RealScalar.of(1.4)));
    AssertFail.of(() -> interpolation.get(RealScalar.ONE));
  }
}
