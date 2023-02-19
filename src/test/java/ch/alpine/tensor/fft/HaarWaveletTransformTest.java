// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.lie.KroneckerProduct;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.pow.Power;

class HaarWaveletTransformTest {
  @Test
  void testSimple() {
    Tensor x = RandomVariate.of(DiscreteUniformDistribution.of(-10, 10), 4);
    Tensor r = HaarWaveletTransform.of(x);
    Tensor s = Tensors.fromString("{{1,1,1,0},{1,1,-1,0},{1,-1,0,1},{1,-1,0,-1}}");
    assertEquals(r, s.dot(x));
  }

  private static Tensor _W(int n) {
    if (n == 1)
      return Tensors.fromString("{{1}}");
    int m = n / 2;
    Tensor a = KroneckerProduct.of(_W(m), Tensors.fromString("{{1},{1}}"));
    Tensor b = KroneckerProduct.of(IdentityMatrix.of(m), Tensors.fromString("{{1},{-1}}"));
    return Join.of(1, a, b);
  }

  @Test
  void testMatrices() {
    assertEquals(_W(2), Tensors.fromString("{{1,1},{1,-1}}"));
    assertEquals(_W(4), Tensors.fromString("{{1,1,1,0},{1,1,-1,0},{1,-1,0,1},{1,-1,0,-1}}"));
  }

  @RepeatedTest(5)
  void testMore(RepetitionInfo repetitionInfo) {
    int n = 1 << repetitionInfo.getCurrentRepetition();
    Tensor x = RandomVariate.of(DiscreteUniformDistribution.of(-10, 10), n);
    Tensor matrix = _W(n);
    assertEquals(matrix.dot(x), HaarWaveletTransform.of(x));
    if (2 < n)
      assertEquals(Power.of(2, n - 1), Det.of(matrix));
  }

  @Test
  void testMatrixSignal() {
    Tensor x = HilbertMatrix.of(16, 9);
    HaarWaveletTransform.of(x);
  }

  @Test
  void testLengthFail() {
    assertThrows(Exception.class, () -> HaarWaveletTransform.of(Tensors.empty()));
    assertThrows(Exception.class, () -> HaarWaveletTransform.of(UnitVector.of(10, 1)));
    assertThrows(Exception.class, () -> HaarWaveletTransform.of(UnitVector.of(12, 1)));
    assertThrows(Exception.class, () -> HaarWaveletTransform.of(UnitVector.of(13, 1)));
  }
}
