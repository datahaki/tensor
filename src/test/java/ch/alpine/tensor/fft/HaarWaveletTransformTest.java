// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ArrayFlatten;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.lie.KroneckerProduct;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.pow.Power;
import test.wrap.DFTConsistency;

class HaarWaveletTransformTest {
  @Test
  void testSimple() {
    Tensor x = RandomVariate.of(DiscreteUniformDistribution.of(-10, 10), 4);
    Tensor r = HaarWaveletTransform.FORWARD.transform(x);
    Tensor s = Tensors.fromString("{{1,1,1,0},{1,1,-1,0},{1,-1,0,1},{1,-1,0,-1}}");
    assertEquals(r, s.dot(x));
  }

  @Test
  void testMatrices() {
    assertEquals(HaarWaveletTransform.FORWARD.matrix(2), Tensors.fromString("{{1,1},{1,-1}}"));
    assertEquals(HaarWaveletTransform.FORWARD.matrix(4), Tensors.fromString("{{1,1,1,0},{1,1,-1,0},{1,-1,0,1},{1,-1,0,-1}}"));
  }

  @RepeatedTest(5)
  void testMore(RepetitionInfo repetitionInfo) {
    int n = 1 << repetitionInfo.getCurrentRepetition();
    Tensor x = RandomVariate.of(DiscreteUniformDistribution.of(-10, 10), n);
    Tensor matrix = HaarWaveletTransform.FORWARD.matrix(n);
    Tensor invers = HaarWaveletTransform.INVERSE.matrix(n);
    assertEquals(Dot.of(matrix, invers), IdentityMatrix.of(n));
    Tensor y = HaarWaveletTransform.FORWARD.transform(x);
    assertEquals(x, HaarWaveletTransform.INVERSE.transform(y));
    assertEquals(matrix.dot(x), y);
    if (2 < n) {
      Scalar scalar = Power.of(2, n - 1);
      assertEquals(scalar, Det.of(matrix));
      Tensor inverse = Inverse.of(matrix);
      SquareMatrixQ.INSTANCE.require(inverse);
    }
  }

  @RepeatedTest(2)
  void test1_4_13(RepetitionInfo repetitionInfo) {
    int m = 1 << repetitionInfo.getCurrentRepetition();
    // int n = 2 * m;
    Tensor wm = HaarWaveletTransform.FORWARD.matrix(m);
    // Tensor wn = HaarWaveletTransform.FORWARD.matrix(n);
    Tensor id = IdentityMatrix.of(m);
    Tensor lhs = ArrayFlatten.of(new Tensor[][] { { wm, id }, { wm, id.negate() } });
    // System.out.println(Pretty.of(lhs));
    Tensor w2 = HaarWaveletTransform.FORWARD.matrix(2);
    Tensor ze = Array.zeros(m, m);
    Tensor m1 = KroneckerProduct.of(w2, id);
    // System.out.println(Pretty.of(m1));
    Tensor m2 = ArrayFlatten.of(new Tensor[][] { { wm, ze }, { ze, id } });
    Tensor rhs = m1.dot(m2);
    assertEquals(lhs, rhs); // (1.4.13)
  }

  @ParameterizedTest
  @EnumSource
  void testConsistency(HaarWaveletTransform haarWaveletTransform) {
    DFTConsistency.checkComplex(haarWaveletTransform, false);
  }

  @Test
  void testInverse() {
    assertEquals(HaarWaveletTransform.FORWARD.inverse(), HaarWaveletTransform.INVERSE);
    assertEquals(HaarWaveletTransform.INVERSE.inverse(), HaarWaveletTransform.FORWARD);
  }

  @Test
  void testMatrixSignal() {
    Tensor x = HilbertMatrix.of(16, 9);
    Tensor y = HaarWaveletTransform.FORWARD.transform(x);
    assertEquals(x, HaarWaveletTransform.INVERSE.transform(y));
  }

  @ParameterizedTest
  @EnumSource
  void testLengthFail(HaarWaveletTransform haarWaveletTransform) {
    assertThrows(Exception.class, () -> haarWaveletTransform.transform(Pi.VALUE));
    assertThrows(Exception.class, () -> haarWaveletTransform.transform(Tensors.empty()));
    assertThrows(Exception.class, () -> haarWaveletTransform.transform(UnitVector.of(10, 1)));
    assertThrows(Exception.class, () -> haarWaveletTransform.transform(UnitVector.of(12, 1)));
    assertThrows(Exception.class, () -> haarWaveletTransform.transform(UnitVector.of(13, 1)));
    assertThrows(Exception.class, () -> haarWaveletTransform.matrix(3));
  }
}
