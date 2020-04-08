// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class LogGammaTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.of(0.2, 4.5);
    for (int count = 0; count < 100; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Scalar logGamma1 = LogGamma.FUNCTION.apply(x);
      Scalar logGamma2 = Log.FUNCTION.apply(Gamma.FUNCTION.apply(x));
      Tolerance.CHOP.requireClose(logGamma1, logGamma2);
    }
  }

  private static void _check(Scalar z, Scalar expect) {
    Scalar result = LogGamma.FUNCTION.apply(z);
    Tolerance.CHOP.requireClose(result, expect);
  }

  public void testMathematica() {
    _check( //
        ComplexScalar.of(3.2363893230567875, 2.665896822743508), //
        ComplexScalar.of(-0.21662285857756203, 3.028323956886318));
    _check( //
        ComplexScalar.of(+0.2, -3.2), //
        ComplexScalar.of(-4.455761789013493, -0.04978161106944923));
    _check( //
        ComplexScalar.of(+1.5, +3.2), //
        ComplexScalar.of(-2.9323985022616412, +1.9509796800608825));
    _check( //
        ComplexScalar.of(+2.7, +8.4), //
        ComplexScalar.of(-7.570209329344152, +12.652654015719346));
  }

  public void testNegativeMathematica() {
    _check(ComplexScalar.of(-0.2, +0.3), ComplexScalar.of(+1.0734581009508424, -2.429743746722234)); // mathematica
    _check(ComplexScalar.of(-0.3, -0.2), ComplexScalar.of(+1.2259360301379854, +2.789379730848464)); // mathematica
    // not consistent with Mathematica
    // _check(ComplexScalar.of(-0.7, -.2), ComplexScalar.of(+1.2068474867912489, +3.483554001467165)); // mathematica
    // _check(ComplexScalar.of(-0.4, 1.0), ComplexScalar.of(-0.7317745484911662, -2.7553511672879787)); // mathematica
    // _check(ComplexScalar.of(-0.4, 0.5), ComplexScalar.of(+0.4757895613772085, -2.863218144225338)); // mathematica
    // _check(ComplexScalar.of(-1, 1), ComplexScalar.of(-0.997496789581829, -4.228631137454775)); // mathematica
    // _check(ComplexScalar.of(-1.2, 2.3), ComplexScalar.of(-4.235543307932702, -3.623795254074647)); // mathematica
  }

  public void testComplex() {
    Distribution distribution = UniformDistribution.of(0.2, 3.5);
    for (int count = 0; count < 100; ++count) {
      Scalar re = RandomVariate.of(distribution);
      Scalar im = RandomVariate.of(distribution);
      Scalar x = ComplexScalar.of(re, im);
      Scalar gamma1 = Exp.FUNCTION.apply(LogGamma.FUNCTION.apply(x));
      Scalar gamma2 = Gamma.FUNCTION.apply(x);
      Tolerance.CHOP.requireClose(gamma1, gamma2);
    }
  }
}
