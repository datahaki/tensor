// code by jph
package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

public record InterpolationQ(Interpolation interpolation) {
  public void checkAll() {
    checkMatch();
    checkMatchExact();
    getScalarFail();
  }

  public void checkMatch() {
    Distribution distribution = UniformDistribution.of(0, 2);
    for (int count = 0; count < 10; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      assertEquals( //
          interpolation.get(Tensors.of(scalar)), //
          interpolation.at(scalar));
    }
  }

  public void checkMatchExact() {
    Distribution distribution = DiscreteUniformDistribution.of(0, 2);
    for (int count = 0; count < 10; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      assertEquals( //
          interpolation.get(Tensors.of(scalar)), //
          interpolation.at(scalar));
    }
  }

  public void getScalarFail() {
    assertThrows(Exception.class, () -> interpolation.get(RealScalar.of(1.4)));
    assertThrows(Exception.class, () -> interpolation.get(RealScalar.ONE));
  }
}
