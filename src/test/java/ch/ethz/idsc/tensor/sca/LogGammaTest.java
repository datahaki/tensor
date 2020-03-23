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

  public void testMathematica() {
    Scalar z = ComplexScalar.of(3.2363893230567875, 2.665896822743508);
    Scalar result = LogGamma.FUNCTION.apply(z);
    Scalar expect = ComplexScalar.of(-0.21662285857756203, 3.028323956886318);
    Tolerance.CHOP.requireClose(result, expect);
  }

  public void testNegativeMathematica() {
    Scalar z = ComplexScalar.of(-0.2, 0.3);
    Scalar result = LogGamma.FUNCTION.apply(z);
    Scalar expect = ComplexScalar.of(1.0734581009508424, -2.429743746722234);
    Tolerance.CHOP.requireClose(result, expect);
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
