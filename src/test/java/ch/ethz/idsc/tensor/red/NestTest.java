// code by jph
package ch.ethz.idsc.tensor.red;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.num.Series;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Gamma;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NestTest extends TestCase {
  public void testPolynomial() {
    Tensor actual = Nest.of( //
        scalar -> Power.of(scalar.add(RealScalar.ONE), RealScalar.of(2)), RealScalar.of(1), 3);
    ExactTensorQ.require(actual);
    assertEquals(RealScalar.of(676), actual);
  }

  public void testSeries() {
    Tensor actual = Nest.of(Series.of(Tensors.vector(1, 2, 1)), RealScalar.ONE, 3);
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
