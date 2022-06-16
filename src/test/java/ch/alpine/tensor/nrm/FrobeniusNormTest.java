// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.fft.FourierMatrix;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Chop;

class FrobeniusNormTest {
  @Test
  void testVector() {
    Scalar norm = FrobeniusNorm.of(Tensors.vector(3, 4));
    assertEquals(norm, RealScalar.of(5));
  }

  @Test
  void testBetween() {
    Tensor t1 = Tensors.fromString("{0, {1, 2}, 3}");
    Tensor t2 = Tensors.fromString("{2, {-1, 0}, 8}");
    Scalar d1 = FrobeniusNorm.between(t1, t2);
    Scalar d2 = Vector2Norm.between(Tensor.of(t1.flatten(-1)), Tensor.of(t2.flatten(-1)));
    assertEquals(d1, d2);
  }

  @Test
  void testMatrix() {
    Scalar norm = FrobeniusNorm.of(IdentityMatrix.of(4));
    assertEquals(norm, RealScalar.of(2));
  }

  @Test
  void testMatrixComplex() {
    Scalar norm = FrobeniusNorm.of(FourierMatrix.of(25));
    assertInstanceOf(RealScalar.class, norm);
  }

  @Test
  void testRank3() {
    Scalar expected = RealScalar.of(2.449489742783178);
    Scalar norm = FrobeniusNorm.of(LeviCivitaTensor.of(3));
    Chop._14.requireClose(norm, expected);
    Scalar scalar = FrobeniusNorm.of(Array.of(i -> RealScalar.ONE, 6));
    Chop._14.requireClose(scalar, expected);
  }
}
