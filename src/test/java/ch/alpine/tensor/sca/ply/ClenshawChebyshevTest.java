// adapted from colt by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.bes.BesselI;
import ch.alpine.tensor.sca.bes.BesselJ;
import ch.alpine.tensor.sca.bes.BesselK;
import ch.alpine.tensor.sca.bes.BesselY;

class ClenshawChebyshevTest {
  @RepeatedTest(8)
  void testRandom(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor coeffs = RandomVariate.of(NormalDistribution.standard(), n);
    Scalar x = RandomVariate.of(NormalDistribution.standard());
    ScalarUnaryOperator suo = ClenshawChebyshev.of(coeffs);
    Scalar expect = suo.apply(x);
    Scalar result = RealScalar.ZERO;
    for (int k = 0; k < coeffs.length(); ++k)
      result = result.add(coeffs.Get(k).multiply(Chebyshev.T.of(k).apply(x)));
    Chop._08.requireClose(expect, result);
  }

  @RepeatedTest(8)
  void testRandomUnit(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor coeffs = RandomVariate.of(NormalDistribution.standard(), n).map(s -> Quantity.of(s, "m"));
    Scalar x = RandomVariate.of(NormalDistribution.standard());
    ScalarUnaryOperator suo = ClenshawChebyshev.of(coeffs);
    Scalar expect = suo.apply(x);
    Scalar result = Quantity.of(0, "m");
    for (int k = 0; k < coeffs.length(); ++k)
      result = result.add(coeffs.Get(k).multiply(Chebyshev.T.of(k).apply(x)));
    Chop._08.requireClose(expect, result);
  }

  @RepeatedTest(8)
  void testRandomUnitExact(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    Tensor coeffs = RandomVariate.of(distribution, n).map(s -> Quantity.of(s, "m"));
    Scalar x = RandomVariate.of(distribution);
    ScalarUnaryOperator suo = ClenshawChebyshev.of(coeffs);
    Scalar expect = suo.apply(x);
    ExactScalarQ.require(expect);
    Scalar result = Quantity.of(0, "m");
    for (int k = 0; k < coeffs.length(); ++k)
      result = result.add(coeffs.Get(k).multiply(Chebyshev.T.of(k).apply(x)));
    assertEquals(expect, result);
    ExactScalarQ.require(result);
  }

  @Test
  void testCoeff0() {
    ScalarUnaryOperator suo = ClenshawChebyshev.of(Tensors.vector(1));
    Scalar z = RandomVariate.of(UniformDistribution.unit(20));
    Tolerance.CHOP.requireClose(RealScalar.ONE, suo.apply(z));
  }

  @Test
  void testCoeff1() {
    ScalarUnaryOperator suo = ClenshawChebyshev.of(Tensors.vector(0, 1));
    Scalar z = RandomVariate.of(UniformDistribution.unit(20));
    assertInstanceOf(DecimalScalar.class, suo.apply(z));
    Tolerance.CHOP.requireClose(z, suo.apply(z));
  }

  @Test
  void testI0() {
    double i0 = BesselI._0(2.3).number().doubleValue();
    assertEquals(i0, 2.829605600627585);
  }

  @Test
  void testJ0() {
    double i0 = BesselJ._0(2.3).number().doubleValue();
    assertEquals(i0, 0.055539786578263826); // !
    // ............. 0.055539784445602064);
  }

  @Test
  void testJ1() {
    double i1 = BesselJ._1(2.3).number().doubleValue();
    assertEquals(i1, 0.5398725327300683);
    // ............. 0.5398725326043137
  }

  @Test
  void testJ2() {
    double i1 = BesselJ.of(2, 2.3).number().doubleValue();
    // System.out.println(i1);
    // ............. 0.41391459173206196
    assertEquals(i1, 0.41391458970875217);
  }

  @Test
  void testK0() {
    double i0 = BesselK._0(2.3).number().doubleValue();
    assertEquals(i0, 0.07913993300209365); // !
    // ............. 0.07913993300209367
  }

  @Test
  void testK1() {
    double i1 = BesselK._1(2.3).number().doubleValue();
    assertEquals(i1, 0.09498244384536267);
    // ............. 0.09498244384536267
  }

  @Test
  void testK2() {
    double i1 = BesselK.kn(2, 2.3);
    assertEquals(i1, 0.16173336243284375);
    // ............. 0.1617333624328438
  }

  @Test
  void testY0() {
    double i0 = BesselY._0(2.3).number().doubleValue();
    // System.out.println(i0);
    assertEquals(i0, 0.5180753919477299); // !
    // ............. 0.5180753962076221
  }

  @Test
  void testY1() {
    double i1 = BesselY._1(2.3).number().doubleValue();
    // System.out.println(i1);
    assertEquals(i1, 0.0522773155615776);
    // ............. 0.05227731584422475
  }

  @Test
  void testY5() {
    double i1 = BesselY.of(5, 2.3).number().doubleValue();
    // System.out.println(i1);
    assertEquals(i1, -5.4143236590500425);
    // ............. -5.414323703733118
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> ClenshawChebyshev.of(HilbertMatrix.of(2)));
    assertThrows(Exception.class, () -> ClenshawChebyshev.of(Tensors.fromString("{1,2,{3},4}")));
    assertThrows(Exception.class, () -> ClenshawChebyshev.of(Pi.VALUE));
  }
}
