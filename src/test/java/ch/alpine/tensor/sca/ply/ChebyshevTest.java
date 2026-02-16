// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.tmp.ResamplingMethod;
import ch.alpine.tensor.tmp.TimeSeries;
import ch.alpine.tensor.tmp.TimeSeriesIntegrate;
import ch.alpine.tensor.tmp.TsEntry;

class ChebyshevTest {
  @Test
  void testFirst() {
    assertEquals(Chebyshev.T.of(0).coeffs(), Tensors.vector(1));
    assertEquals(Chebyshev.T.of(1).coeffs(), Tensors.vector(0, 1));
    assertEquals(Chebyshev.T.of(2).coeffs(), Tensors.vector(-1, 0, 2));
    assertEquals(Chebyshev.T.of(3).coeffs(), Tensors.vector(0, -3, 0, 4));
    assertEquals(Chebyshev.T.of(4).coeffs(), Tensors.vector(1, 0, -8, 0, 8));
    assertEquals(Chebyshev.T.of(6).coeffs(), Tensors.vector(-1, 0, 18, 0, -48, 0, 32));
  }

  @Test
  void testUFirst() {
    assertEquals(Chebyshev.U.of(0).coeffs(), Tensors.vector(1));
    assertEquals(Chebyshev.U.of(1).coeffs(), Tensors.vector(0, 2));
    assertEquals(Chebyshev.U.of(2).coeffs(), Tensors.vector(-1, 0, 4));
    assertEquals(Chebyshev.U.of(3).coeffs(), Tensors.vector(0, -4, 0, 8));
    assertEquals(Chebyshev.U.of(4).coeffs(), Tensors.vector(1, 0, -12, 0, 16));
    assertEquals(Chebyshev.U.of(6).coeffs(), Tensors.vector(-1, 0, 24, 0, -80, 0, 64));
  }

  @ParameterizedTest
  @EnumSource
  void testUAntider(Chebyshev chebyshev) {
    for (int n = 0; n < 10; ++n) {
      Polynomial polynomial = chebyshev.of(n).antiderivative();
      Scalar ip1 = polynomial.apply(RealScalar.ONE);
      Scalar in1 = polynomial.apply(RealScalar.ONE.negate());
      ExactScalarQ.require(ip1);
      ExactScalarQ.require(in1);
      if (Integers.isOdd(n))
        assertTrue(Scalars.isZero(ip1.subtract(in1)));
    }
  }

  @RepeatedTest(10)
  void testProduct() {
    int n = ThreadLocalRandom.current().nextInt(100);
    int m = ThreadLocalRandom.current().nextInt(100);
    Polynomial p1 = Chebyshev.T.of(n).times(Chebyshev.T.of(m));
    Polynomial p2 = Chebyshev.T.of(n + m).plus(Chebyshev.T.of(Math.abs(m - n))).times(Rational.HALF);
    assertEquals(p1, p2);
  }

  @RepeatedTest(10)
  void testProductUT() {
    int n = ThreadLocalRandom.current().nextInt(100);
    int m = ThreadLocalRandom.current().nextInt(100);
    Polynomial p1 = Chebyshev.T.of(m).times(Chebyshev.U.of(n));
    Polynomial p2 = null;
    if (n >= m) {
      p2 = Chebyshev.U.of(n + m).plus(Chebyshev.U.of(n - m)).times(Rational.HALF);
      assertEquals(p1, p2);
    }
    if (n <= m - 2) {
      p2 = Chebyshev.U.of(n + m).minus(Chebyshev.U.of(m - n - 2)).times(Rational.HALF);
      assertEquals(p1, p2);
    }
  }

  @Test
  void testComplex() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(Pi.VALUE));
    for (int n = 1; n < 10; ++n) {
      Scalar ab = ComplexScalar.fromPolar(RealScalar.ONE, RandomVariate.of(distribution));
      final int fn = n;
      ScalarUnaryOperator suo = z -> ComplexScalar.of( //
          Chebyshev.T.of(fn).apply(Re.FUNCTION.apply(z)), //
          Chebyshev.U.of(fn - 1).apply(Re.FUNCTION.apply(z)).multiply(Im.FUNCTION.apply(z)));
      Scalar c1 = suo.apply(ab);
      Scalar c2 = Power.of(ab, n);
      Tolerance.CHOP.requireClose(c1, c2);
    }
  }

  @ParameterizedTest
  @EnumSource
  void testFail(Chebyshev chebyshev) {
    assertThrows(Exception.class, () -> chebyshev.of(-1));
  }

  @RepeatedTest(3)
  void testOrthogonal(RepetitionInfo repetitionInfo) {
    ScalarUnaryOperator w = s -> Sqrt.FUNCTION.apply(RealScalar.ONE.subtract(s.multiply(s))).reciprocal();
    Tensor domain = ChebyshevNodes._1.of(11 + repetitionInfo.getCurrentRepetition() * 3);
    for (int k = 0; k < 7; ++k) {
      for (int j = k; j < 7; ++j) {
        Polynomial p1 = Chebyshev.T.of(k);
        Polynomial p2 = Chebyshev.T.of(j);
        TimeSeries timeSeries = TimeSeries.of(domain.stream() //
            .map(Scalar.class::cast) //
            .map(t -> new TsEntry(t, Times.of(w.apply(t), p1.apply(t), p2.apply(t)))), //
            ResamplingMethod.LINEAR_INTERPOLATION);
        Scalar result = (Scalar) TimeSeriesIntegrate.of(timeSeries, timeSeries.domain());
        // System.out.println(k+" "+j+" "+result);
        if (k != j)
          Tolerance.CHOP.requireZero(result);
      }
      // System.out.println("---");
    }
  }
}
