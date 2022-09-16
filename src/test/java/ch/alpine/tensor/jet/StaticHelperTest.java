// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.DagumDistribution;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.pdf.c.GompertzMakehamDistribution;
import ch.alpine.tensor.pdf.c.GumbelDistribution;
import ch.alpine.tensor.pdf.c.LaplaceDistribution;
import ch.alpine.tensor.pdf.c.LogisticDistribution;
import ch.alpine.tensor.pdf.c.ParetoDistribution;
import ch.alpine.tensor.pdf.c.RayleighDistribution;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.ply.Polynomial;

class StaticHelperTest {
  @Test
  void testPolynomial() {
    Tensor coeffs = Tensors.vector(2, 1, 3, 4);
    Polynomial f0 = Polynomial.of(coeffs);
    Polynomial f1 = f0.derivative();
    Scalar value = RationalScalar.of(3, 17);
    Tensor gnd = Tensors.of(f0.apply(value), f1.apply(value));
    Scalar scalar = JetScalar.of(value, gnd.length());
    JetScalar der = (JetScalar) f0.apply(scalar);
    assertEquals(der.vector(), gnd);
  }

  @Test
  void testPolynomialRandom() {
    Tensor c0 = RandomVariate.of(DiscreteUniformDistribution.of(-3, 3), 4);
    Polynomial f0 = Polynomial.of(c0);
    Polynomial f1 = f0.derivative();
    Polynomial f2 = f1.derivative();
    Scalar value = RationalScalar.of(3, 17);
    Tensor gnd = Tensors.of(f0.apply(value), f1.apply(value), f2.apply(value));
    Scalar scalar = JetScalar.of(value, gnd.length());
    JetScalar der = (JetScalar) f0.apply(scalar);
    assertEquals(der.vector(), gnd);
  }

  public static final Distribution[] DISTRIBUTIONS = { //
      ExponentialDistribution.of(RationalScalar.HALF), //
      ParetoDistribution.of(RationalScalar.HALF, RealScalar.ONE), //
      UniformDistribution.of(1, 10), //
      TrapezoidalDistribution.of(0, 4, 5, 8), //
      DagumDistribution.of(1, 2, 3), //
      GompertzMakehamDistribution.of(1, 2), //
      GumbelDistribution.of(1, 2), //
      LaplaceDistribution.of(1, 2), //
      LogisticDistribution.of(1, 2), //
      RayleighDistribution.of(2) //
  };

  @Test
  void testDistributions() {
    for (Distribution distribution : DISTRIBUTIONS) {
      CDF cdf = CDF.of(distribution);
      PDF pdf = PDF.of(distribution);
      for (Tensor _x : Subdivide.of(2, 4, 6)) {
        Scalar x = JetScalar.of((Scalar) _x, 4);
        JetScalar js1 = (JetScalar) cdf.p_lessEquals(x);
        JetScalar js2 = (JetScalar) pdf.at(x);
        Tolerance.CHOP.requireClose(js1.vector().extract(1, 4), js2.vector().extract(0, 3));
      }
    }
  }
}
