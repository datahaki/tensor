// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.BinomialDistribution;

public class BasisTransformTest {
  @Test
  public void testDimensions() {
    int n = 3;
    Tensor s = BasisTransform.ofForm(Array.zeros(n, n, n, n), Array.zeros(n, n + 2));
    assertEquals(Dimensions.of(s), Arrays.asList(5, 5, 5, 5));
  }

  @Test
  public void testFormRank2() {
    int n = 4;
    Distribution distribution = BinomialDistribution.of(80, 0.3);
    Tensor form = RandomVariate.of(distribution, n, n);
    Tensor v = RandomVariate.of(distribution, n, n);
    Tensor s = BasisTransform.ofForm(form, v);
    assertEquals(s, Transpose.of(v).dot(form).dot(v));
  }

  @Test
  public void testForm() {
    int rows = 6;
    int cols = 8;
    Random random = new Random(4);
    Tensor m = IdentityMatrix.of(rows);
    Tensor v = Tensors.matrix((i, j) -> RealScalar.of(random.nextInt(10)), rows, cols);
    Tensor t = BasisTransform.ofForm(m, v);
    Tensor d = t.subtract(Transpose.of(t));
    assertEquals(d, Array.zeros(cols, cols));
    Tensor g = BasisTransform.of(m, 0, v);
    assertEquals(t, g);
  }

  @Test
  public void testStream() {
    int n = 5;
    Integer[] asd = new Integer[n];
    IntStream.range(0, n).forEach(i -> asd[i] = (i + 1) % n);
    assertEquals(asd[0].intValue(), 1);
    assertEquals(asd[n - 1].intValue(), 0);
  }

  @Test
  public void testMatrix() {
    Random random = new Random(3);
    int n = 5;
    Distribution distribution = BinomialDistribution.of(10, 0.3);
    Tensor matrix = RandomVariate.of(distribution, random, n, n);
    Tensor v = RandomVariate.of(distribution, random, n, n);
    if (Scalars.nonZero(Det.of(v))) {
      Tensor trafo1 = BasisTransform.ofMatrix(matrix, v);
      ExactTensorQ.require(trafo1);
      Tensor trafo2 = BasisTransform.of(matrix, 1, v);
      ExactTensorQ.require(trafo2);
      assertEquals(trafo1, trafo2);
    }
  }

  @Test
  public void testAd() {
    Tensor v = HilbertMatrix.of(3);
    Tensor _a = LeviCivitaTensor.of(3).negate();
    Tensor ad = BasisTransform.of(_a, 1, v);
    Tensor he = BasisTransform.of(ad, 1, Inverse.of(v));
    assertEquals(he, _a);
  }

  @Test
  public void testAdTypeFail() {
    Tensor v = HilbertMatrix.of(3);
    assertThrows(IllegalArgumentException.class, () -> BasisTransform.of(Array.zeros(3, 3, 3), -1, v));
  }

  @Test
  public void testAdInverseFail() {
    Tensor v = Array.zeros(3);
    assertThrows(IllegalArgumentException.class, () -> BasisTransform.of(Array.zeros(3, 3, 3), 1, v));
  }

  @Test
  public void testFormVectorFail() {
    int n = 3;
    assertThrows(IndexOutOfBoundsException.class, () -> BasisTransform.ofForm(Array.zeros(n, n, n), Array.zeros(n)));
  }

  @Test
  public void testMatrixFail() {
    assertThrows(TensorRuntimeException.class, () -> BasisTransform.ofMatrix(IdentityMatrix.of(3), DiagonalMatrix.of(1, 1, 0)));
  }
}
