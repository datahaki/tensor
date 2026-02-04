// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.fft.Fourier;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.sv.SingularValueDecompositionWrap;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class FrobeniusNormTest {
  @Test
  void testVector() {
    Scalar norm = FrobeniusNorm.of(Tensors.vector(3, 4));
    assertEquals(norm, RealScalar.of(5));
  }

  @Test
  void testVectorSquared() {
    Scalar norm = FrobeniusNorm.squared(Tensors.vector(3, 4));
    assertEquals(norm, RealScalar.of(25));
  }

  @Test
  void testBetween() {
    Tensor t1 = Tensors.fromString("{0, {1, 2}, 3}");
    Tensor t2 = Tensors.fromString("{2, {-1, 0}, 8}");
    Scalar d1 = FrobeniusNorm.of(t1.subtract(t2));
    Scalar d2 = Vector2Norm.between(Flatten.of(t1), Flatten.of(t2));
    assertEquals(d1, d2);
  }

  @ParameterizedTest
  @CsvSource({ "1,1", "4,2", "9,3" })
  void testMatrix(int dim, int fn) {
    Scalar norm = FrobeniusNorm.of(IdentityMatrix.of(dim));
    assertEquals(norm, RealScalar.of(fn));
  }

  @Test
  void testMatrixComplex() {
    Scalar norm = FrobeniusNorm.of(Fourier.FORWARD.matrix(25));
    assertInstanceOf(RealScalar.class, norm);
    Tolerance.CHOP.requireClose(norm, RealScalar.of(5));
  }

  @Test
  void testRank3() {
    Scalar expected = RealScalar.of(2.449489742783178);
    Scalar norm = FrobeniusNorm.of(LeviCivitaTensor.of(3));
    Chop._14.requireClose(norm, expected);
    Scalar scalar = FrobeniusNorm.of(Array.of(_ -> RealScalar.ONE, 6));
    Chop._14.requireClose(scalar, expected);
  }

  @Test
  void testSvd() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 7, 4);
    Scalar v1 = FrobeniusNorm.of(matrix);
    Scalar v2 = Vector2Norm.of(SingularValueDecompositionWrap.of(matrix).values());
    Tolerance.CHOP.requireClose(v1, v2);
  }
}
