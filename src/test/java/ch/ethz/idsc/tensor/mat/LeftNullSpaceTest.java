// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.mat.re.Det;
import ch.ethz.idsc.tensor.num.GaussScalar;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class LeftNullSpaceTest extends TestCase {
  public void testRankDeficient() {
    Tensor matrix = Tensors.fromString("{{0, 1}, {0, 1}, {0, 1}, {0, 1}}");
    Tensor nullsp = LeftNullSpace.of(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(3, 4));
    Chop._10.requireAllZero(nullsp.dot(matrix));
  }

  public void testMaxRank() {
    Tensor matrix = Tensors.fromString("{{0, 1}, {2, 1}, {0, 1}, {0, 1}}");
    Tensor nullsp = LeftNullSpace.of(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(2, 4));
    Chop._10.requireAllZero(nullsp.dot(matrix));
  }

  public void testRankDeficientTranspose() {
    Tensor matrix = Tensors.fromString("{{0, 0, 0, 0}, {1, 1, 1, 1}}");
    Tensor nullsp = LeftNullSpace.of(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(1, 2));
    Chop._10.requireAllZero(nullsp.dot(matrix));
  }

  public void testMaxRankTranspose() {
    Tensor matrix = Tensors.fromString("{{0, 2, 0, 0}, {1, 1, 1, 1}}");
    Tensor nullsp = LeftNullSpace.of(matrix);
    assertEquals(Dimensions.of(nullsp), Arrays.asList(0));
  }

  private static void _matrix(Tensor A) {
    assertTrue(NullSpace.of(A).stream().map(vector -> A.dot(vector)).allMatch(Chop.NONE::allZero));
    assertTrue(LeftNullSpace.of(A).stream().map(vector -> vector.dot(A)).allMatch(Chop.NONE::allZero));
    _matrixNumeric(A);
  }

  private static void _matrixNumeric(Tensor A) {
    assertTrue(NullSpace.of(A.map(N.DOUBLE)).stream().map(vector -> A.dot(vector)).allMatch(Chop._12::allZero));
    assertTrue(LeftNullSpace.of(A.map(N.DOUBLE)).stream().map(vector -> vector.dot(A)).allMatch(Chop._12::allZero));
    assertTrue(NullSpace.usingQR(A.map(N.DOUBLE)).stream().map(vector -> A.dot(vector)).allMatch(Chop._12::allZero));
  }

  private static void _check(Tensor A) {
    _matrix(A);
    _matrix(Transpose.of(A));
  }

  public void testBulk() {
    _check(Tensors.fromString("{{0, 0}}"));
    _check(Tensors.fromString("{{0, 1}}"));
    _check(Tensors.fromString("{{1, 0, 3}}"));
    _check(Tensors.fromString("{{1[m], 0[m], 3[m]}}"));
    _check(Tensors.fromString("{{0, 0}, {0, 2}, {1, 0}}"));
    _check(Tensors.fromString("{{0, 0}, {0, 3}, {1, 0}, {0, 0}}"));
    _check(Tensors.fromString("{{0, 0}, {0, 0}, {0, 0}, {0, 0}}"));
    _check(Tensors.fromString("{{0, 0}, {0, 0}, {1, 0}, {0, 0}}"));
    _check(Tensors.fromString("{{0, 0, 1}, {0, 0, 0}, {1, 0, 0}}").map(s -> Quantity.of(s, "m")));
    _check(Tensors.fromString("{{0, 0, 0}, {0, 0, 0}, {1, 0, 0}, {3, 2, 0}}").map(s -> Quantity.of(s, "m")));
    _check(Tensors.fromString("{{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}}").map(s -> Quantity.of(s, "m")));
    _check(Tensors.fromString("{{0, 5, 1}, {0, 0, 0}, {1, 0, 0}, {3, 2, 0}}").map(s -> Quantity.of(s, "m")));
  }

  public void testRandom() {
    Distribution distribution = NormalDistribution.standard();
    Tensor x = RandomVariate.of(distribution, 3);
    Tensor y = RandomVariate.of(distribution, 7);
    _matrixNumeric(TensorProduct.of(x, y));
    _matrixNumeric(TensorProduct.of(y, x));
  }

  public void testGaussScalar() {
    Random random = new Random();
    int prime = 7741;
    int dim1 = 6;
    Tensor matrix = Array.fill(() -> GaussScalar.of(random.nextInt(), prime), dim1 - 2, dim1);
    Tensor identi = DiagonalMatrix.of(Unprotect.dimension1(matrix), GaussScalar.of(1, prime));
    List<Integer> list = Dimensions.of(identi);
    assertEquals(list, Arrays.asList(dim1, dim1));
    Tensor nullsp = NullSpace.usingRowReduce(matrix);
    Scalar det = Det.of(matrix);
    assertEquals(det, GaussScalar.of(0, prime));
    assertEquals(Dimensions.of(nullsp), Arrays.asList(2, 6));
  }

  public void testRectangle3x2G() {
    ScalarUnaryOperator suo = scalar -> GaussScalar.of(scalar.number().longValue(), 7);
    Tensor matrix = Tensors.fromString("{{1, 0}, {0, 0}, {0, 0}}").map(suo);
    Tensor tensor = NullSpace.usingRowReduce(matrix);
    assertEquals(tensor.get(0), UnitVector.of(2, 1).map(suo));
    assertTrue(Scalars.isZero(Det.of(matrix)));
  }

  public void testRectangle2x3G() {
    ScalarUnaryOperator suo = scalar -> GaussScalar.of(scalar.number().longValue(), 7);
    Tensor matrix = Tensors.fromString("{{1, 0, 0}, {0, 0, 0}}").map(suo);
    Tensor tensor = NullSpace.usingRowReduce(matrix);
    assertEquals(tensor.get(0), UnitVector.of(3, 1).map(suo));
    assertEquals(tensor.get(1), UnitVector.of(3, 2).map(suo));
    assertTrue(Scalars.isZero(Det.of(matrix)));
  }

  private static Tensor deprec(Tensor vector, Tensor nullsp) {
    return vector.dot(PseudoInverse.usingSvd(nullsp)).dot(nullsp);
  }

  public void testQR() {
    Distribution distribution = UniformDistribution.unit();
    for (int count = 0; count < 10; ++count) {
      Tensor vector = RandomVariate.of(distribution, 10);
      Tensor design = RandomVariate.of(distribution, 10, 3);
      Tensor nullsp = LeftNullSpace.usingQR(design);
      Tensor p1 = deprec(vector, nullsp);
      Tensor p2 = Dot.of(nullsp, vector, nullsp);
      Tolerance.CHOP.requireClose(p1, p2);
    }
  }

  public void testLeftGaussScalar() {
    int prime = 7879;
    Random random = new Random();
    Tensor matrix = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(), prime), 7, 4);
    Tensor nullsp = LeftNullSpace.of(matrix);
    assertEquals(nullsp.length(), 3);
    for (Tensor vector : nullsp)
      Chop.NONE.requireAllZero(Dot.of(vector, matrix));
  }
}
