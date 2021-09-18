// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.num.Polynomial;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Gamma;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NestTest extends TestCase {
  public void testPolynomial() {
    Tensor actual = Nest.of( //
        scalar -> Power.of(scalar.add(RealScalar.ONE), RealScalar.of(2)), RealScalar.of(1), 3);
    ExactTensorQ.require(actual);
    assertEquals(RealScalar.of(676), actual);
  }

  public void testSeries() {
    Tensor actual = Nest.of(Polynomial.of(Tensors.vector(1, 2, 1)), RealScalar.ONE, 3);
    ExactTensorQ.require(actual);
    assertEquals(RealScalar.of(676), actual);
  }

  public void testGamma() {
    Scalar expected = ComplexScalar.of(0.024484718696096586, -0.3838080212320521);
    Scalar actual = Nest.of(Gamma.FUNCTION, ComplexScalar.of(0.3, 0.9), 3);
    Chop._08.requireClose(expected, actual);
  }

  public void testCopy() {
    Tensor in = Array.zeros(2);
    Tensor re = Nest.of(null, in, 0);
    re.set(RealScalar.ONE::add, Tensor.ALL);
    assertFalse(in.equals(re));
    assertEquals(in, Array.zeros(2));
  }

  public void testFail() {
    AssertFail.of(() -> Nest.of(Cos.FUNCTION, RealScalar.of(0.3), -1));
  }
}
