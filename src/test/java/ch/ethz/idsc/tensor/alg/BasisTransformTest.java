// code by jph
package ch.ethz.idsc.tensor.alg;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.pdf.BinomialDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BasisTransformTest extends TestCase {
  public void testDimensions() {
    int n = 3;
    Tensor s = BasisTransform.ofForm(Array.zeros(n, n, n, n), Array.zeros(n, n + 2));
    assertEquals(Dimensions.of(s), Arrays.asList(5, 5, 5, 5));
  }

  public void testFormRank2() {
    int n = 4;
    Distribution distribution = BinomialDistribution.of(80, 0.3);
    Tensor form = RandomVariate.of(distribution, n, n);
    Tensor v = RandomVariate.of(distribution, n, n);
    Tensor s = BasisTransform.ofForm(form, v);
    assertEquals(s, Transpose.of(v).dot(form).dot(v));
  }

  public void testForm() {
    int rows = 6;
    int cols = 8;
    Random random = new SecureRandom();
    Tensor m = IdentityMatrix.of(rows);
    Tensor v = Tensors.matrix((i, j) -> RealScalar.of(random.nextInt(10)), rows, cols);
    Tensor t = BasisTransform.ofForm(m, v);
    Tensor d = t.subtract(Transpose.of(t));
    assertEquals(d, Array.zeros(cols, cols));
    Tensor g = BasisTransform.of(m, 0, v);
    assertEquals(t, g);
  }

  public void testStream() {
    int n = 5;
    Integer[] asd = new Integer[n];
    IntStream.range(0, n).forEach(i -> asd[i] = (i + 1) % n);
    assertEquals(asd[0].intValue(), 1);
    assertEquals(asd[n - 1].intValue(), 0);
  }

  public void testMatrix() {
    Distribution distribution = BinomialDistribution.of(20, 0.3);
    Tensor matrix = RandomVariate.of(distribution, 5, 5);
    Tensor v = RandomVariate.of(distribution, 5, 5);
    Tensor trafo1 = BasisTransform.ofMatrix(matrix, v);
    ExactTensorQ.require(trafo1);
    Tensor trafo2 = BasisTransform.of(matrix, 1, v);
    ExactTensorQ.require(trafo2);
    assertEquals(trafo1, trafo2);
  }

  public void testAd() {
    Tensor v = HilbertMatrix.of(3);
    Tensor _a = LeviCivitaTensor.of(3).negate();
    Tensor ad = BasisTransform.of(_a, 1, v);
    Tensor he = BasisTransform.of(ad, 1, Inverse.of(v));
    assertEquals(he, _a);
  }

  public void testAdTypeFail() {
    Tensor v = HilbertMatrix.of(3);
    AssertFail.of(() -> BasisTransform.of(Array.zeros(3, 3, 3), -1, v));
  }

  public void testAdInverseFail() {
    Tensor v = Array.zeros(3);
    AssertFail.of(() -> BasisTransform.of(Array.zeros(3, 3, 3), 1, v));
  }

  public void testFormVectorFail() {
    int n = 3;
    AssertFail.of(() -> BasisTransform.ofForm(Array.zeros(n, n, n), Array.zeros(n)));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> BasisTransform.ofMatrix(IdentityMatrix.of(3), DiagonalMatrix.of(1, 1, 0)));
  }
}
