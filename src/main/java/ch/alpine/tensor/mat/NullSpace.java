// code by jph
package ch.alpine.tensor.mat;

import java.util.stream.IntStream;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.mat.pd.Orthogonalize;
import ch.alpine.tensor.mat.qr.GramSchmidt;
import ch.alpine.tensor.mat.re.RowReduce;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;

/** {@link NullSpace#of(Tensor)} picks the most suited algorithm to determine the
 * nullspace of a given matrix.
 * 
 * <p>Three methods are available:
 * 
 * <ul>
 * <li>{@link LeftNullSpace#usingRowReduce(Tensor)}
 * <li>{@link NullSpace#usingQR(Tensor)}
 * <li>{@link NullSpace#usingSvd(Tensor)}
 * </ul>
 * 
 * <p>Let N = NullSpace[A]. If N is non-empty, then A.Transpose[N] == 0.
 * 
 * Careful: The above equation is also the Mathematica convention for matrices with complex
 * entries, where the user might be tempted to erroneously use conjugate transpose.
 * 
 * <p>Quote from Wikipedia:
 * For matrices whose entries are floating-point numbers, the problem of computing the kernel
 * makes sense only for matrices such that the number of rows is equal to their rank:
 * because of the rounding errors, a floating-point matrix has almost always a full rank,
 * even when it is an approximation of a matrix of a much smaller rank. Even for a full-rank
 * matrix, it is possible to compute its kernel only if it is well conditioned, i.e. it has a
 * low condition number.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/NullSpace.html">NullSpace</a>
 * 
 * @see MatrixDotTranspose
 * @see LeftNullSpace */
public enum NullSpace {
  ;
  /** if matrix has any entry in machine precision, i.e. {@link FiniteScalarQ} returns true,
   * the nullspace is computed using {@link SingularValueDecomposition}.
   * In that case the vectors in the return value are normalized.
   * 
   * <p>If all entries of the given matrix are in exact precision,
   * the nullspace is computed using {@link RowReduce}.
   * In that case the vectors in the return value are <em>not</em> normalized.
   * 
   * <p>Function is consistent with Mathematica.
   * 
   * @param matrix
   * @return list of vectors that span the nullspace; if the nullspace is trivial, then
   * the return value is the empty tensor {}
   * @throws Exception if given parameter is not a matrix */
  public static Tensor of(Tensor matrix) {
    if (ExactTensorQ.of(matrix))
      return usingRowReduce(matrix);
    int rows = matrix.length();
    int cols = Unprotect.dimension1Hint(matrix);
    boolean isComplex = Flatten.stream(matrix, 1) //
        .anyMatch(scalar -> scalar instanceof ComplexScalar);
    return rows < cols || isComplex //
        ? usingQR(matrix)
        : usingSvd(matrix);
  }

  /** @param matrix of dimensions n x m with exact precision entries
   * @return tensor of vectors that span the kernel of given matrix */
  /* package */ static Tensor usingRowReduce(Tensor matrix) {
    return LeftNullSpace.usingRowReduce(Transpose.of(matrix));
  }

  /** N = NullSpace[A]. If N is non-empty, then A.Transpose[N] == 0.
   * 
   * @param matrix of any dimensions possibly with complex entries
   * @return list of orthogonal vectors that span the nullspace
   * @see OrthogonalMatrixQ */
  public static Tensor usingQR(Tensor matrix) {
    Tensor r = GramSchmidt.of(matrix).getR();
    int n = Unprotect.dimension1Hint(matrix);
    return Orthogonalize.of(Join.of(r, IdentityMatrix.of(n))).extract(r.length(), n).maps(Conjugate.FUNCTION);
  }

  /** @param matrix of dimensions rows x cols with rows >= cols
   * @return (cols - rank()) x cols matrix
   * @throws Exception if given matrix has rows &lt; cols */
  public static Tensor usingSvd(Tensor matrix) {
    return of(SingularValueDecomposition.of(matrix));
  }

  /** @param svd
   * @param chop
   * @return tensor of vectors that span the kernel of given matrix */
  public static Tensor of(SingularValueDecomposition svd, Chop chop) {
    return Tensor.of(IntStream.range(0, svd.values().length()) //
        .filter(index -> Scalars.isZero(chop.apply(svd.values().Get(index)))) //
        .mapToObj(index -> svd.getV().get(Tensor.ALL, index)));
  }

  /** @param svd
   * @return tensor of vectors that span the kernel of given matrix */
  public static Tensor of(SingularValueDecomposition svd) {
    return of(svd, Tolerance.CHOP);
  }
}
