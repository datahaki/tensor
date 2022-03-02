// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixRankTest extends TestCase {
  /** @param matrix with numeric precision entries
   * @return rank of matrix */
  public static int usingSvd(Tensor matrix) {
    return of(SingularValueDecomposition.of(Unprotect.dimension1Hint(matrix) <= matrix.length() //
        ? matrix
        : Transpose.of(matrix)));
  }

  /** @param svd
   * @param chop threshold
   * @return rank of matrix decomposed in svd */
  public static int of(SingularValueDecomposition svd, Chop chop) {
    return Math.toIntExact(svd.values().stream() //
        .map(Scalar.class::cast) //
        .map(chop) //
        .filter(Scalars::nonZero) //
        .count());
  }

  /** @param svd
   * @return rank of matrix decomposed in svd */
  public static int of(SingularValueDecomposition svd) {
    return of(svd, Tolerance.CHOP);
  }

  // ---
  public void testRank() {
    assertEquals(MatrixRank.usingRowReduce(Tensors.of(Tensors.vector(0, 0, 0))), 0);
    assertEquals(MatrixRank.usingRowReduce(Tensors.of(Tensors.vector(0, 1, 0))), 1);
    assertEquals(MatrixRank.usingRowReduce(Tensors.of( //
        Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 0))), 1);
    assertEquals(MatrixRank.usingRowReduce(Tensors.of( //
        Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 1))), 2);
    assertEquals(MatrixRank.of(Tensors.of( //
        Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 1))), 2);
  }

  public void testNumeric() {
    Tensor m = Tensors.of( //
        Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 1e-40));
    assertEquals(MatrixRank.usingRowReduce(m), 2);
    assertEquals(usingSvd(m), 1);
    assertEquals(MatrixRank.of(m), 1); // <- numeric
  }

  public void testNumeric2() {
    Tensor m = Transpose.of(Tensors.of( //
        Tensors.vector(0, 1, 0), Tensors.vector(0, 1, 1e-40)));
    assertEquals(MatrixRank.usingRowReduce(m), 2);
    assertEquals(usingSvd(m), 1);
    assertEquals(MatrixRank.of(m), 1); // <- numeric
  }

  public void testNumeric3() {
    Tensor matrix = Tensors.fromString("{{0, 1.0, 0}, {0, 1, 1/1000000000000000000000000000000000000}}");
    assertEquals(MatrixRank.usingRowReduce(matrix), 2);
    assertEquals(usingSvd(matrix), 1);
    assertEquals(MatrixRank.of(matrix), 1); // <- numeric
  }

  public void testExact() {
    Tensor matrix = Tensors.fromString("{{0, 1, 0}, {0, 1, 1/1000000000000000000000000000000000000}}");
    assertEquals(MatrixRank.usingRowReduce(matrix), 2);
    assertEquals(usingSvd(matrix), 1); // <- numeric
    assertEquals(MatrixRank.of(matrix), 2); // <- exact
  }

  public void testZeros() {
    Tensor matrix = Array.zeros(9, 5);
    SingularValueDecomposition singularValueDecomposition = SingularValueDecomposition.of(matrix);
    int rank = of(singularValueDecomposition);
    assertEquals(rank, 0);
  }

  public void testVectorFail() {
    AssertFail.of(() -> MatrixRank.of(Tensors.vector(1, 2, 3)));
  }
}
