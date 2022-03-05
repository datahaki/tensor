// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class CotTest extends TestCase {
  public void testReal() {
    Scalar scalar = RealScalar.of(1.23);
    Scalar res = Cot.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(res, RealScalar.of(0.3546331016766021));
  }

  public void testComplex() {
    Scalar scalar = ComplexScalar.of(-1.23, 3);
    Scalar res = Cot.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(res, Scalars.fromString("-0.003111382117752743` - 0.9961526961973638` *I"));
  }

  public void testTensor() {
    Tensor vector = Tensors.vector(-1, 2, -3);
    Tensor expected = Tensors.fromString("{-0.6420926159343306`, -0.45765755436028577`, 7.015252551434534`}");
    Tolerance.CHOP.requireClose(Cot.of(vector), expected);
  }

  private static void checkCot(double value) {
    Scalar z = RealScalar.of(value);
    Scalar h = Cos.FUNCTION.apply(z).divide(Sin.FUNCTION.apply(z));
    Scalar s = Cot.FUNCTION.apply(RealScalar.of(value));
    assertEquals(s, h);
  }

  public void testPiHalf() {
    final double pi_h = Math.PI / 2;
    checkCot(pi_h);
    checkCot(Math.nextUp(pi_h));
  }
}
