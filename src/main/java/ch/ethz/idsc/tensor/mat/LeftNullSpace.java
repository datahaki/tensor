// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.QRDecomposition;

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
        ? usingRowReduce(matrix)
        : usingQR(matrix);
  }

  /** @param matrix
   * @return list of vectors that span the left nullspace of given matrix */
  public static Tensor usingRowReduce(Tensor matrix) {
    return usingRowReduce(matrix, IdentityMatrix.of(matrix.length()));
  }

  /** @param matrix
   * @param identity
   * @return list of vectors that span the left nullspace of given matrix */
  public static Tensor usingRowReduce(Tensor matrix, Tensor identity) {
    final int rows = matrix.length(); // == identity.length()
    final int cols = Unprotect.dimension1(matrix);
    Tensor lhs = RowReduce.of(Join.of(1, matrix, identity));
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
    Tensor qinv = qrDecomposition.getInverseQ();
    // return Tensor.of(IntStream.range(0, qinv.length()) //
    // .filter(i->cols<=i || Tolerance.CHOP.allZero(r.Get(i, i))) //
    // .mapToObj(qinv::get));
    if (IntStream.range(0, cols).mapToObj(i -> r.Get(i, i)).map(Tolerance.CHOP).anyMatch(Scalars::isZero)) {
      // LONGTERM implementation is not satisfactory
      // System.out.println("LNS USING SVD");
      Tensor nspace = NullSpace.usingSvd(Transpose.of(qrDecomposition.getR().extract(0, cols)));
      // System.out.println(Pretty.of(nspace.map(Round._4)));
      Tensor upper = Tensor.of(qinv.stream().limit(cols));
      return Tensor.of(Stream.concat( //
          nspace.stream().map(row -> row.dot(upper)), //
          qinv.stream().skip(cols)));
    }
    return Tensor.of(qinv.stream().skip(cols));
  }
}
