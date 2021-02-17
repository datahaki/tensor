// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.fft.FourierMatrix;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class FrobeniusNormTest extends TestCase {
  public void testVector() {
    Scalar norm = FrobeniusNorm.of(Tensors.vector(3, 4));
    assertEquals(norm, RealScalar.of(5));
  }

  public void testBetween() {
    Tensor t1 = Tensors.fromString("{0, {1, 2}, 3}");
    Tensor t2 = Tensors.fromString("{2, {-1, 0}, 8}");
    Scalar d1 = FrobeniusNorm.between(t1, t2);
    Scalar d2 = Vector2Norm.between(Tensor.of(t1.flatten(-1)), Tensor.of(t2.flatten(-1)));
    assertEquals(d1, d2);
  }

  public void testMatrix() {
    Scalar norm = FrobeniusNorm.of(IdentityMatrix.of(4));
    assertEquals(norm, RealScalar.of(2));
  }

  public void testMatrixComplex() {
    Scalar norm = FrobeniusNorm.of(FourierMatrix.of(25));
    assertTrue(norm instanceof RealScalar);
  }

  public void testRank3() {
    Scalar expected = RealScalar.of(2.449489742783178);
    Scalar norm = FrobeniusNorm.of(LeviCivitaTensor.of(3));
    Chop._14.requireClose(norm, expected);
    Scalar scalar = FrobeniusNorm.of(Array.of(i -> RealScalar.ONE, 6));
    Chop._14.requireClose(scalar, expected);
  }
}
