// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.QRDecomposition;

/** LeftNullSpace[matrix] == NullSpace[Transpose[matrix]]
 * 
 * the tensor library provides LeftNullSpace for convenience.
 * LeftNullSpace does not exist in Mathematica.
 * 
 * @see NullSpace */
public enum LeftNullSpace {
  ;
  /** @param matrix
   * @return list of vectors that span the left nullspace of given matrix */
  public static Tensor of(Tensor matrix) {
    int rows = matrix.length();
    int cols = Unprotect.dimension1(matrix);
    if (rows <= cols)
      return NullSpace.usingSvd(Transpose.of(matrix));
    QRDecomposition qrDecomposition = QRDecomposition.of(matrix);
    Tensor r = qrDecomposition.getR();
    Tensor qinv = qrDecomposition.getInverseQ();
    boolean full = true;
    for (int i = 0; i < cols && full; ++i)
      full &= Scalars.nonZero(Tolerance.CHOP.apply(r.Get(i, i)));
    if (!full) {
      Tensor nspace = NullSpace.usingSvd(Transpose.of(qrDecomposition.getR().extract(0, cols)));
      Tensor upper = Tensor.of(qinv.stream().limit(cols));
      return Tensor.of(Stream.concat( //
          nspace.stream().map(row -> row.dot(upper)), //
          qinv.stream().skip(cols)));
    }
    return Tensor.of(qinv.stream().skip(cols));
  }
}
