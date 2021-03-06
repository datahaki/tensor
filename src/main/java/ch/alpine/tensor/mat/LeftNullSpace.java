// code by jph
package ch.alpine.tensor.mat;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.qr.QRDecomposition;
import ch.alpine.tensor.mat.re.Pivot;
import ch.alpine.tensor.mat.re.Pivots;
import ch.alpine.tensor.mat.re.RowReduce;

/** Let N = NullSpace[A]. If N is non-empty, then N.A == 0.
 * 
 * <pre>
 * LeftNullSpace[matrix] == NullSpace[Transpose[matrix]]
 * </pre>
 * 
 * <p>The tensor library provides LeftNullSpace for convenience.
 * The command "LeftNullSpace" does not exist in Mathematica.
 * 
 * @see NullSpace */
public enum LeftNullSpace {
  ;
  /** @param matrix
   * @return list of vectors that span the left nullspace of given matrix */
  public static Tensor of(Tensor matrix) {
    return ExactTensorQ.of(matrix) //
        ? usingRowReduce(matrix, Pivots.FIRST_NON_ZERO)
        : usingQR(matrix);
  }

  /** @param matrix
   * @return */
  public static Tensor usingRowReduce(Tensor matrix) {
    return usingRowReduce(matrix, Pivots.selection(matrix));
  }

  /** @param matrix
   * @param pivot
   * @return list of vectors that span the left nullspace of given matrix */
  private static Tensor usingRowReduce(Tensor matrix, Pivot pivot) {
    int rows = matrix.length();
    Tensor lhs = RowReduce.of(Join.of(1, matrix, DiagonalMatrix.of(rows, matrix.Get(0, 0).one())), pivot);
    int cols = Unprotect.dimension1Hint(matrix);
    int j = 0;
    int c0 = 0;
    while (c0 < cols && j < rows)
      if (Scalars.nonZero(lhs.Get(j, c0++))) // <- careful: c0 is modified
        ++j;
    return Tensor.of(lhs.extract(j, rows).stream().map(row -> row.extract(cols, cols + rows)));
  }

  /** @param matrix of any dimensions
   * @return list of orthogonal vectors that span the left nullspace
   * @see OrthogonalMatrixQ */
  public static Tensor usingQR(Tensor matrix) {
    int rows = matrix.length();
    int cols = Unprotect.dimension1(matrix);
    if (rows <= cols)
      return NullSpace.usingSvd(Transpose.of(matrix));
    QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
    Tensor r = qrDecomposition.getR();
    Tensor qinv = qrDecomposition.getQTranspose();
    boolean nonRankMax = IntStream.range(0, cols) //
        .mapToObj(i -> r.Get(i, i)) //
        .anyMatch(Tolerance.CHOP::isZero);
    if (nonRankMax) {
      Tensor nspace = NullSpace.usingSvd(Transpose.of(qrDecomposition.getR().extract(0, cols)));
      Tensor upper = Tensor.of(qinv.stream().limit(cols));
      return Tensor.of(Stream.concat( //
          nspace.stream().map(row -> row.dot(upper)), //
          qinv.stream().skip(cols)));
    }
    return Tensor.of(qinv.stream().skip(cols)); // matrix has maximal rank
  }
}
